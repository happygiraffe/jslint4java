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
import com.googlecode.jslint4java.formatter.CheckstyleXmlFormatter;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;

public class CheckstyleXmlResultFormatter implements ResultFormatter {
    private final JSLintResultFormatter form = new CheckstyleXmlFormatter();
    private final StringBuilder sb = new StringBuilder();
    private OutputStream out;

    public void begin() {
        if (out == null)
            throw new BuildException("must specify destFile for xml output");
        // Clear, just in case this object gets reused.
        if (sb.length() > 0) {
            sb.delete(0, sb.length() - 1);
        }
        if (form.header() != null) {
            sb.append(form.header());
        }
    }

    public void end() {
        if (form.footer() != null) {
            sb.append(form.footer());
        }
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
    }

    public void output(JSLintResult result) {
        sb.append(form.format(result));
    }

    public void setFile(File file) {
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        }
    }

    public void setStdout(OutputStream defaultOutputStream) {
        // Ignore, we never want to write to stdout.
    }

}
