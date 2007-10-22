package net.happygiraffe.jslint.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.happygiraffe.jslint.Issue;
import net.happygiraffe.jslint.JSLint;
import net.happygiraffe.jslint.Option;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
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
 *       &lt;fileset dir=&quot;.&quot; includes=&quot;*.js&quot; excludes=&quot;*.pack.js&quot; /&gt;
 *     &lt;/jsl:jslint&gt;
 *   &lt;/target&gt;
 * &lt;/project
 * </pre>
 *
 * @author dom
 * @version $Id$
 * @see <a href="http://jslint.com/">jslint.com</a>
 */
public class JSLintTask extends Task {

    private List<FileSet> filesets = new ArrayList<FileSet>();

    private JSLint lint;

    /**
     * Check the contents of this {@link FileSet}.
     *
     * @param fileset
     */
    public void add(FileSet fileset) {
        filesets.add(fileset);
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

    /**
     * Scan the specified directory for JavaScript files and lint them.
     */
    @Override
    public void execute() throws BuildException {
        if (filesets.size() == 0)
            throw new BuildException("no filesets specified");

        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            for (String fileName : ds.getIncludedFiles()) {
                lintFile(new File(ds.getBasedir(), fileName));
            }
        }
    }

    private void lintFile(File file) {
        try {
            log("check " + file, Project.MSG_VERBOSE);
            // XXX We should allow specifying the encoding here.
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file)));
            List<Issue> issues = lint.lint(file.toString(), reader);
            if (issues.size() > 0) {
                for (Issue issue : issues) {
                    log(issue.toString());
                    log(issue.getEvidence());
                    log(spaces(issue.getCharacter()) + "^");
                }
            }
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Set the options for running JSLint.
     */
    public void setOptions(String optionList) throws BuildException {
        for (String name : optionList.split("\\s*,\\s*")) {
            try {
                // The Option constants are upper case…
                lint.addOption(Option.valueOf(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BuildException("Unknown option " + name);
            }
        }
    }

    private String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
