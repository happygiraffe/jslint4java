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

public class ReportWriterTest {

    private static final String REPORT_XML = "report.xml";

    @Rule
    public TemporaryFolder tmpf = new TemporaryFolder();

    private final JSLintResultFormatter formatter = new JSLintXmlFormatter();

    private ReportWriter createReportWriter(String filename) {
        return new ReportWriter(new File(tmpf.getRoot(), filename), formatter);
    }

    @Test
    public void createsNonEmptyFile() {
        ReportWriter rw = createReportWriter(REPORT_XML);
        rw.open();
        rw.close();
        assertThat(rw.getReportFile().exists(), is(true));
        assertThat(rw.getReportFile().length(), is(not(0L)));
    }

    @Test
    public void makesParentDirectory() {
        File parent = new File(tmpf.getRoot(), "somedir");
        ReportWriter rw = new ReportWriter(new File(parent, REPORT_XML), formatter);
        rw.open();
        rw.close();
        assertThat(parent.exists(), is(true));
    }

    private String readReport(ReportWriter rw) throws FileNotFoundException, IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(rw.getReportFile()),
                    Charset.forName("UTF-8"));
            return IOUtil.toString(reader);
        } finally {
            IOUtil.close(reader);
        }
    }

    @Test
    public void reportContentsSanity() throws Exception {
        ReportWriter rw = createReportWriter(REPORT_XML);
        rw.open();
        // Create a result with no problems.
        rw.report(new ResultBuilder("foo.js").build());
        rw.close();
        String report = readReport(rw);
        assertThat(report, containsString("<jslint>"));
        assertThat(report, containsString("<file name='foo.js'>"));
        assertThat(report, containsString("</jslint>"));
    }
}
