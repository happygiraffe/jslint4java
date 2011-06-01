package com.googlecode.jslint4java.maven;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.JSLintXmlFormatter;

public class ReportWriterTest {

    @Rule
    public TemporaryFolder tmpf = new TemporaryFolder();

    private final JSLintResultFormatter formatter = new JSLintXmlFormatter();

    private ReportWriter createReportWriter(String filename) {
        return new ReportWriter(new File(tmpf.getRoot(), filename), formatter);
    }

    @Test
    public void createsNonEmptyFile() {
        ReportWriter rw = createReportWriter("report.xml");
        rw.open();
        rw.close();
        assertThat(rw.getReportFile().exists(), is(true));
        assertThat(rw.getReportFile().length(), is(not(0L)));
    }
}
