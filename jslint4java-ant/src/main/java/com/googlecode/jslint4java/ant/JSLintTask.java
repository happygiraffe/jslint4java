package com.googlecode.jslint4java.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.Option;
import com.googlecode.jslint4java.UnicodeBomInputStream;

/**
 * Run {@link JSLint} over a tree of files in order to pick holes in your
 * JavaScript.
 *
 * <p>
 * Example build.xml usage:
 *
 * <pre>
 * &lt;project name=&quot;build-test&quot; xmlns:jsl=&quot;antlib:com.googlecode.jslint4java&quot;&gt;
 *   &lt;target name=&quot;jslint&quot;&gt;
 *     &lt;jsl:jslint options=&quot;undef&quot;&gt;
 *       &lt;formatter type=&quot;plain&quot; /&gt;
 *       &lt;fileset dir=&quot;.&quot; includes=&quot;*.js&quot; excludes=&quot;*.pack.js&quot; /&gt;
 *     &lt;/jsl:jslint&gt;
 *   &lt;/target&gt;
 * &lt;/project
 * </pre>
 *
 * <p>
 * You have to specify one or more nested <i>fileset</i> elements. You may
 * optionally specify a <i>formatter</i> element in order to generate output (as
 * opposed to just a build failed message). Usually, you will want the plain
 * formatter, but in case you want to generate a report, the xml formatter
 * mighht be useful.
 *
 * <h3>Attributes</h3>
 *
 * <dl>
 * <dt><code>encoding</code></dt>
 * <dd>Optional. The encoding of the JavaScript files. Defaults to system
 * encoding.</dd>
 * <dt><code>haltOnFailure</code></dt>
 * <dd>Optional. Specify if the build should fail if there are files which do
 * not pass JSLint. Defaults to true.</dd>
 * <dt><code>options</code></dt>
 * <dd>Optional. A comma separated list of {@link Option} names. No default.</dd>
 * </dl>
 *
 * @author dom
 * @see <a href="http://jslint.com/">jslint.com< /a>
 * @see FormatterElement
 */
public class JSLintTask extends Task {

    private static final String NO_FILES_TO_LINT = "no files to lint!";

    private final Union resources = new Union();

    private final List<ResultFormatter> formatters = new ArrayList<ResultFormatter>();

    private boolean haltOnFailure = true;

    private String encoding = System.getProperty("file.encoding", "UTF-8");

    private String failureProperty = null;

    private File jslintSource = null;

    private final Map<Option, String> options = new HashMap<Option, String>();

    private PredefElement predef = null;

    /**
     * Check the contents of this {@link ResourceCollection}.
     *
     * @param rc
     *            Any kind of resource collection, e.g. fileset.
     */
    public void add(ResourceCollection rc) {
        resources.add(rc);
    }

    /**
     * Add in a {@link ResultFormatter} through the medium of a
     * {@link FormatterElement}.
     *
     * @param fe
     */
    public void addConfiguredFormatter(FormatterElement fe) {
        fe.setDefaultOutputStream(getDefaultOutput());
        formatters.add(fe.getResultFormatter());
    }

    /**
     * Capture a predef element.
     */
    public void addPredef(PredefElement predef) {
        this.predef = predef;
    }

    public void applyOptions(JSLint lint) {
        for (Entry<Option, String> entry : options.entrySet()) {
            String value = entry.getValue();
            try {
                if (value == null) {
                    lint.addOption(entry.getKey());
                } else {
                    lint.addOption(entry.getKey(), value);
                }
            } catch (IllegalArgumentException e) {
                String optName = entry.getKey().getLowerName();
                String className = e.getClass().getName();
                throw new BuildException(optName + ": " + className + ": " + e.getMessage());
            }
        }
        // Handle predefs separately. They don't work too well in the options
        // string.
        if (predef != null) {
            lint.addOption(Option.PREDEF, predef.getText());
        }
    }

    /**
     * Scan the specified directories for JavaScript files and lint them.
     */
    @Override
    public void execute() throws BuildException {
        if (resources.size() == 0) {
            // issue 53: this isn't a fail, just a notice.
            log(NO_FILES_TO_LINT);
        }

        JSLint lint = makeLint();
        applyOptions(lint);

        for (ResultFormatter rf : formatters) {
            rf.begin();
        }

        int failedCount = 0;
        int totalErrorCount = 0;
        for (Resource resource : resources.listResources()) {
            try {
                int errorCount = lintStream(lint, resource);
                if (errorCount > 0) {
                    totalErrorCount += errorCount;
                    failedCount++;
                }
            } catch (IOException e) {
                throw new BuildException(e);
            }
        }

        for (ResultFormatter rf : formatters) {
            rf.end();
        }

        if (failedCount != 0) {
            String msg = failureMessage(failedCount, totalErrorCount);
            if (haltOnFailure) {
                throw new BuildException(msg);
            } else {
                log(msg);
                if (failureProperty != null) {
                    getProject().setProperty(failureProperty, msg);
                }
            }
        }
    }

    private String failureMessage(int failedCount, int totalErrorCount) {
        return "JSLint: " + totalErrorCount + " " + plural(totalErrorCount, "error") + " in "
                + failedCount + " " + plural(failedCount, "file");
    }

    /**
     * Return a logging {@link OutputStream} that can be passed to formatters.
     *
     * @return
     */
    private OutputStream getDefaultOutput() {
        return new LogOutputStream(this, Project.MSG_INFO);
    }

    /**
     * Lint a given stream. Closes the stream after use.
     *
     * @param lint
     *
     * @throws IOException
     */
    private int lintStream(JSLint lint, Resource resource) throws UnsupportedEncodingException,
            IOException {
        BufferedReader reader = null;
        try {
            UnicodeBomInputStream stream = new UnicodeBomInputStream(resource.getInputStream());
            stream.skipBOM();
            reader = new BufferedReader(new InputStreamReader(stream, encoding));
            String name = resource.toString();
            JSLintResult result = lint.lint(name, reader);
            log("Found " + result.getIssues().size() + " issues in " + name, Project.MSG_VERBOSE);
            for (ResultFormatter rf : formatters) {
                rf.output(result);
            }
            return result.getIssues().size();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Create a new {@link JSLint} object.
     */
    public JSLint makeLint() throws BuildException {
        try {
            if (jslintSource == null) {
                return new JSLintBuilder().fromDefault();
            } else {
                return new JSLintBuilder().fromFile(jslintSource);
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Quick and nasty hack to pluralise words. Works enough for my needs.
     */
    private String plural(int count, String word) {
        return count == 1 ? word : word + "s";
    }

    /**
     * Set the encoding of the source files that JSLint will read. If not
     * specified, the default is the system encoding (via the
     * <code>file.encoding</code> property). If that isn't present, default to
     * <code>UTF-8</code>.
     *
     * @param encoding
     *            a valid charset identifier.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * The name of a property to set upon failure. This property will contain
     * the log message.
     */
    public void setFailureProperty(String failureProperty) {
        this.failureProperty = failureProperty;
    }

    /**
     * Specify an alternative version of jslint.
     */
    public void setJslint(File jslint) {
        jslintSource = jslint;
    }

    /**
     * Should the build stop if JSLint fails? Defaults to true.
     *
     * @param haltOnFailure
     */
    public void setHaltOnFailure(boolean haltOnFailure) {
        this.haltOnFailure = haltOnFailure;
    }

    /**
     * Set the options for running JSLint. This is a comma separated list of
     * {@link Option} names. The names are case-insensitive.
     *
     * <p>
     * NB: If you want to put an {@link Option#PREDEF} in here, you should use a
     * {@code <predef>} child element instead. Otherwise, it could be difficult
     * to specify a comma separated list as an element of a comma separated
     * list…
     */
    public void setOptions(String optionList) throws BuildException {
        for (String name : optionList.split("\\s*,\\s*")) {
            String[] parts = name.split("=", 2);
            String optName = parts[0];
            try {
                // The Option constants are upper case…
                Option o = Option.valueOf(optName.toUpperCase(Locale.getDefault()));
                // If an argument has been specified, use it.
                String value = parts.length == 2 ? parts[1] : null;
                options.put(o, value);
            } catch (IllegalArgumentException e) {
                throw new BuildException("Unknown option " + optName);
            }
        }
    }
}
