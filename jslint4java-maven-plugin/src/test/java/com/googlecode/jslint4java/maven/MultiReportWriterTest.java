package com.googlecode.jslint4java.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.PlainFormatter;

public class MultiReportWriterTest {

    @Rule
    public TemporaryFolder tmpf = new TemporaryFolder();

    private void assertFileNotEmpty(File file) {
        assertThat(file + " exists", file.exists(), is(true));
        assertThat(file + " is not empty", file.length() > 0, is(true));
    }

    private ReportWriter makeReportWriter(File aFile) {
        assertThat(aFile.exists(), is(false));
        return new ReportWriterImpl(aFile, new PlainFormatter());
    }

    private JSLintResult makeResult() {
        String filename = "foo.js";
        Issue issue = new Issue.IssueBuilder(filename, 1, 1, "bad code").build();
        return new JSLintResult.ResultBuilder(filename).addIssue(issue).build();
    }

    @Test
    public void shouldWriteToMultipleFiles() throws Exception {
        File aFile = new File(tmpf.getRoot(), "a");
        ReportWriter aWriter = makeReportWriter(aFile);

        File bFile = new File(tmpf.getRoot(), "b");
        ReportWriter bWriter = makeReportWriter(bFile);

        ReportWriter multi = new MultiReportWriter(aWriter, bWriter);
        multi.open();
        multi.report(makeResult());
        multi.close();

        assertFileNotEmpty(aFile);
        assertFileNotEmpty(bFile);
    }

}
