package com.googlecode.jslint4java.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.PlainFormatter;

/**
 * Output all JSLint errors to the console. Shows the error, the line on which
 * it occurred and a pointer to the character at which it occurred.
 *
 * <p>
 * If a file is specified, all output will go there. If not, then all output
 * will go to stdout.
 *
 * @author dom
 */
public class PlainResultFormatter implements ResultFormatter {

    protected OutputStream out;
    protected PrintWriter w = null;

    private final JSLintResultFormatter form = new PlainFormatter();

    public void begin() {
        // Use the default system encoding, as that's likely what the console is
        // set to...
        w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
    }

    public void end() {
        FileUtils.close(w);
        w = null;
    }

    /**
     * Emit all issues to the console.
     */
    public void output(JSLintResult result) {
        if (result.getIssues().size() == 0) {
            return;
        }

        w.print(form.format(result));

    }

    public void setFile(File file) {
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        }
    }

    public void setStdout(OutputStream defaultOutputStream) {
        out = defaultOutputStream;
    }
}
