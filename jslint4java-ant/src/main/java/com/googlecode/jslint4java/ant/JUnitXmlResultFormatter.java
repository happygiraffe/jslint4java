package com.googlecode.jslint4java.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.JUnitXmlFormatter;

/**
 * Output the result of a JSLint run as a JUnit XML file.
 *
 * @see JUnitXmlFormatter
 */
public class JUnitXmlResultFormatter implements ResultFormatter {

    private File file;
    private final JSLintResultFormatter form = new JUnitXmlFormatter();

    public void begin() {
        if (file == null) {
            throw new BuildException("must set destFile attribute");
        }
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new BuildException(file + " must be a directory");
            }
        } else {
            if (!file.mkdirs()) {
                throw new BuildException("failed to make directory " + file);
            }
        }
    }

    public void end() {
        file = null;
    }

    public void output(JSLintResult result) {
        File f = new File(file, testFileName(result));
        Writer w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
            if (form.header() != null) {
                w.write(form.header());
            }
            w.write(form.format(result));
            if (form.footer() != null) {
                w.write(form.footer());
            }
        } catch (IOException e) {
            throw new BuildException(e);
        } finally {
            FileUtils.close(w);
        }
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setStdout(OutputStream defaultOutputStream) {
        // Ignored, we must to output to a file.
    }

    /**
     * Make a filename for the test case by following the usual JUnit conventions and replacing non
     * alphanumeric chars with underscores.
     */
    private String testFileName(JSLintResult result) {
        return "TEST-" + result.getName().replaceAll("[^\\w.-]", "_") + ".xml";
    }

}
