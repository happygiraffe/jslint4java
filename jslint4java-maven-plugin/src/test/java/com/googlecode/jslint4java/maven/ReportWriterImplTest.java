package com.googlecode.jslint4java.maven;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.codehaus.plexus.util.IOUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.googlecode.jslint4java.JSLintResult.ResultBuilder;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.JSLintXmlFormatter;

public class ReportWriterImplTest {

    private static final String REPORT_XML = "report.xml";

    @Rule
    public TemporaryFolder tmpf = new TemporaryFolder();

    private final JSLintResultFormatter formatter = new JSLintXmlFormatter();

    private ReportWriterImpl createReportWriter(String filename) {
        return new ReportWriterImpl(new File(tmpf.getRoot(), filename), formatter);
    }

    @Test
    public void createsNonEmptyFile() {
        ReportWriterImpl rw = createReportWriter(REPORT_XML);
        rw.open();
        rw.close();
        assertThat(rw.getReportFile().exists(), is(true));
        assertThat(rw.getReportFile().length(), is(not(0L)));
    }

    @Test
    public void makesParentDirectory() {
        // issue 65: ensure we make *all* intervening directories.
        File parent = new File(new File(tmpf.getRoot(), "some"), "dir");
        ReportWriter rw = new ReportWriterImpl(new File(parent, REPORT_XML), formatter);
        rw.open();
        rw.close();
        assertThat(parent.exists(), is(true));
    }

    /**
     * issue 65: If we configure an invalid report xml, we should blow up with a
     * {@link RuntimeException} (wrapping a
     * {@link FileNotFoundException}). Instead, we're blowing up with an
     * {@link NullPointerException}when we try to close().
     */
    @Test
    public void closeDoesntHideFileNotFoundExceptionWithNullPointerException() throws IOException {
        // This is guaranteed to fail as it's a file not a directory.
        File f  = tmpf.newFile("bob");
        ReportWriter rw = new ReportWriterImpl(new File(f, REPORT_XML), formatter);
        try {
            rw.open();
        } catch (RuntimeException e) {
            // Check we've got the correctly wrapped exception.
            assertThat(e.getCause(), is(FileNotFoundException.class));
        } finally {
            // This shouldn't blow up with an NPE.
            rw.close();
        }
    }

    private String readFile(File reportFile) throws FileNotFoundException, IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(reportFile),
                    Charset.forName("UTF-8"));
            return IOUtil.toString(reader);
        } finally {
            IOUtil.close(reader);
        }
    }

    @Test
    public void reportContentsSanity() throws Exception {
        ReportWriterImpl rw = createReportWriter(REPORT_XML);
        rw.open();
        // Create a result with no problems.
        rw.report(new ResultBuilder("foo.js").build());
        rw.close();
        String report = readFile(rw.getReportFile());
        assertThat(report, containsString("<jslint>"));
        assertThat(report, containsString("<file name='foo.js'>"));
        assertThat(report, containsString("</jslint>"));
    }
}
