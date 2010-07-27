package com.googlecode.jslint4java.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.ReportFormatter;

/**
 * Write JSLint reports to a file full of HTML.
 */
public class ReportResultFormatter implements ResultFormatter {

    private final ReportFormatter formatter = new ReportFormatter();
    private OutputStream out;
    private final StringBuilder sb = new StringBuilder();

    public void begin() {
        if (out == null) {
            throw new BuildException("destFile not specified");
        }
        sb.append("<html>");
        sb.append("<head></head>");
        sb.append("<body>");
    }

    public void end() {
        sb.append("</body></html>");
        Writer w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
            w.write(sb.toString());
            w.flush();
        } catch (IOException exc) {
            throw new BuildException("Unable to write log file", exc);
        } finally {
            FileUtils.close(w);
        }
        out = null;
        sb.delete(0, sb.length() - 1);
    }

    public void output(JSLintResult result) {
        sb.append(formatter.format(result));
    }

    public void setFile(File file) {
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        }
    }

    public void setStdout(OutputStream defaultOutputStream) {
        // Ignore; stdout not supported.
    }

}
