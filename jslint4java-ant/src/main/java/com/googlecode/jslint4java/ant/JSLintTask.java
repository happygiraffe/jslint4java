package com.googlecode.jslint4java.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Union;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintBuilder;
import com.googlecode.jslint4java.Option;

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
 * @version $Id$
 * @see <a href="http://jslint.com/">jslint.com< /a>
 * @see FormatterElement
 */
public class JSLintTask extends Task {

    private final Union resources = new Union();

    private JSLint lint;

    private final List<ResultFormatter> formatters = new ArrayList<ResultFormatter>();

    private boolean haltOnFailure = true;

    private String encoding = System.getProperty("file.encoding", "UTF-8");

    private File jslintSource = null;

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
     * Scan the specified directories for JavaScript files and lint them.
     */
    @Override
    public void execute() throws BuildException {
        if (resources.size() == 0) {
            throw new BuildException("no resources specified");
        }

        for (ResultFormatter rf : formatters) {
            rf.begin();
        }

        int failedCount = 0;
        int totalErrorCount = 0;
        for (Resource resource : resources.listResources()) {
            try {
                int errorCount = lintStream(resource);
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
            }
        }
    }

    private String failureMessage(int failedCount, int totalErrorCount) {
        return "JSLint: " + totalErrorCount + " "
                + plural(totalErrorCount, "error") + " in " + failedCount + " "
                + plural(failedCount, "file");
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
     * Create a new {@link JSLint} object.
     */
    @Override
    public void init() throws BuildException {
        try {
            if (jslintSource == null) {
                lint = new JSLintBuilder().fromDefault();
            } else {
                lint = new JSLintBuilder().fromFile(jslintSource);
            }
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Lint a given stream. Closes the stream after use.
     *
     * @throws IOException
     */
    private int lintStream(Resource resource)
            throws UnsupportedEncodingException, IOException {
        InputStream stream = null;
        try {
            stream = resource.getInputStream();
            String name = resource.toString();
            List<Issue> issues = lint.lint(name, new BufferedReader(
                    new InputStreamReader(stream, encoding)));
            log("Found " + issues.size() + " issues in " + name,
                    Project.MSG_VERBOSE);
            for (ResultFormatter rf : formatters) {
                rf.output(name, issues);
            }
            return issues.size();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Quick and nasty hack to pluralise words.  Works enough for my needs.
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
     */
    public void setOptions(String optionList) throws BuildException {
        for (String name : optionList.split("\\s*,\\s*")) {
            String[] parts = name.split("=", 2);
            String optName = parts[0];
            try {
                // The Option constants are upper case…
                Option o = Option.valueOf(optName.toUpperCase(Locale
                        .getDefault()));
                // If an argument has been specified, use it.
                if (parts.length == 2) {
                    lint.addOption(o, parts[1]);
                } else {
                    lint.addOption(o);
                }
            } catch (IllegalArgumentException e) {
                throw new BuildException("Unknown option " + optName);
            }
        }
    }

}
