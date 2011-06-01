package com.googlecode.jslint4java.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.codehaus.plexus.util.IOUtil;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;

/**
 * Write a report formatted by a {@link JSLintResultFormatter} to a file. You
 * should call the methods in this order:
 * <ol>
 * <li>{@link #open()}
 * <li>{@link #report(JSLintResult)}
 * <li>{@link #close()}
 * </ol>
 *
 * <p>
 * If you don't call close(), you won't see any output.
 */
public class ReportWriter {

    private final File reportFile;
    private final JSLintResultFormatter formatter;
    private BufferedWriter writer;

    public ReportWriter(File reportFile, JSLintResultFormatter formatter) {
        this.reportFile = reportFile;
        this.formatter = formatter;
    }

    /** End the report. */
    public void close() {
        try {
            if (formatter.footer() != null) {
                writer.write(formatter.footer());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.close(writer);
        }
    }

    private void ensureReportDirectoryExists() {
        File parent = reportFile.getAbsoluteFile().getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        }
    }

    // Visible for testing.
    public File getReportFile() {
        return reportFile;
    }

    /** Begin the report. Call immediately after construction. */
    public void open() {
        ensureReportDirectoryExists();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile),
                    Charset.forName("UTF-8")));
            if (formatter.header() != null) {
                writer.write(formatter.header());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Report the result of a run. */
    public void report(JSLintResult result) {
        try {
            writer.write(formatter.format(result));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
