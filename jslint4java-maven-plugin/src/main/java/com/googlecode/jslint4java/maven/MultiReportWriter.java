package com.googlecode.jslint4java.maven;

import java.util.Arrays;
import java.util.List;

import com.google.common.io.Closeables;
import com.googlecode.jslint4java.JSLintResult;

/**
 * Allow writing the same result to multiple places at once.
 * <p>
 * TODO: figure out exception handling
 */
public class MultiReportWriter implements ReportWriter {

    private final List<ReportWriter> reportWriters;

    /** Send a report to all passed in ReportWriters. */
    public MultiReportWriter(ReportWriter... reportWriters) {
        this.reportWriters = Arrays.asList(reportWriters);
    }

    /** Close all contained ReportWriters. */
    public void close() {
        for (ReportWriter reportWriter : reportWriters) {
            Closeables.closeQuietly(reportWriter);
        }
    }

    /** Open all contained ReportWriters. */
    public void open() {
        for (ReportWriter reportWriter : reportWriters) {
            reportWriter.open();
        }
    }

    /** Report on this result for each reportWriter. */
    public void report(JSLintResult result) {
        for (ReportWriter reportWriter : reportWriters) {
            reportWriter.report(result);
        }
    }

}
