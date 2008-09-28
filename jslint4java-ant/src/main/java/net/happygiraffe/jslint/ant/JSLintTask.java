package net.happygiraffe.jslint.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.happygiraffe.jslint.Issue;
import net.happygiraffe.jslint.JSLint;
import net.happygiraffe.jslint.Option;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.FileSet;

/**
 * Run {@link JSLint} over a tree of files in order to pick holes in your
 * JavaScript.
 *
 * <p>
 * Example build.xml usage:
 *
 * <pre>
 * &lt;project name=&quot;build-test&quot; xmlns:jsl=&quot;antlib:net.happygiraffe.jslint&quot;&gt;
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

    private final List<FileSet> filesets = new ArrayList<FileSet>();

    private JSLint lint;

    private final List<ResultFormatter> formatters = new ArrayList<ResultFormatter>();

    private boolean haltOnFailure = true;

    private String encoding = System.getProperty("file.encoding", "UTF-8");

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
     * Check the contents of this {@link FileSet}.
     *
     * @param fileset
     */
    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    /**
     * Scan the specified directories for JavaScript files and lint them.
     */
    @Override
    public void execute() throws BuildException {
        if (filesets.size() == 0) {
            throw new BuildException("no filesets specified");
        }

        for (ResultFormatter rf : formatters) {
            rf.begin();
        }

        int failedCount = 0;
        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            for (String fileName : ds.getIncludedFiles()) {
                if (!lintFile(new File(ds.getBasedir(), fileName))) {
                    failedCount++;
                }
            }
        }

        for (ResultFormatter rf : formatters) {
            rf.end();
        }

        if (failedCount != 0) {
            String files = failedCount == 1 ? "file" : "files";
            String msg = failedCount + " " + files + " did not pass JSLint";
            if (haltOnFailure) {
                throw new BuildException(msg);
            } else {
                log(msg);
            }
        }
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
            lint = new JSLint();
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private boolean lintFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), encoding));
            List<Issue> issues = lint.lint(file.toString(), reader);
            log("Found " + issues.size() + " issues in " + file,
                    Project.MSG_VERBOSE);
            for (ResultFormatter rf : formatters) {
                rf.output(file, issues);
            }
            return issues.size() == 0;
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
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
     * Set the options for running JSLint. This is a comma separated list of
     * {@link Option} names. The names are case-insensitive.
     */
    public void setOptions(String optionList) throws BuildException {
        for (String name : optionList.split("\\s*,\\s*")) {
            String[] parts = name.split("=", 2);
            String optName = parts[0];
            try {
                String value = null;
                if (parts.length == 2) {
                    value = parts[1];
                }
                // The Option constants are upper case…
                Option o = Option.valueOf(optName.toUpperCase(Locale.getDefault()));
                lint.addOption(o.getBinding(value));
            } catch (IllegalArgumentException e) {
                throw new BuildException("Unknown option " + optName);
            }
        }
    }

}
