package net.happygiraffe.jslint.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.happygiraffe.jslint.Issue;
import net.happygiraffe.jslint.JSLint;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class JSLintTask extends MatchingTask {

    private File dir;

    private JSLint lint;

    public void setDir(File dir) {
        this.dir = dir;
    }

    @Override
    public void init() throws BuildException {
        try {
            lint = new JSLint();
        } catch (IOException e) {
            throw new BuildException(e);
        }

        // Default to "*.js" anywhere in dir.
        setIncludes("**/*.js");
    }

    @Override
    public void execute() throws BuildException {
        if (dir == null) {
            throw new BuildException("dir must be specified");
        }

        log("dir = " + dir, Project.MSG_DEBUG);

        DirectoryScanner ds = getDirectoryScanner(dir);
        for (String fileName : ds.getIncludedFiles()) {
            try {
                File file = new File(dir, fileName);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file)));
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

        // Clear out for next time.
        setDir(null);
    }

    private String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
