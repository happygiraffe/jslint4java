package com.googlecode.jslint4java.ant;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.Issue.IssueBuilder;

public class PlainResultFormatterTest {

    private final PlainResultFormatter rf = new PlainResultFormatter();
    private final List<Issue> issues = new ArrayList<Issue>();
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * Run the formatter over the current set of issues. The File as input is
     * just a convenient way of passing a name & path together.
     */
    private void runFormatter(File file) {
        rf.begin();
        rf.output(file.getName(), issues);
        rf.end();
    }

    @Before
    public void setUpOutputStream() {
        rf.setOut(out);
    }

    @Test
    public void testExpectedOutputNoIssues() {
        File file = new File("foo/bar.js");
        runFormatter(file);
        assertThat(out.size(), is(0));
    }

    @Test
    public void testExpectedOutputOneIssue() {
        File file = new File("foo/bar.js");
        Issue issue = new IssueBuilder(file.toString(), 1, 1, "no clucking")
                .evidence("cluck()").build();
        issues.add(issue);
        runFormatter(file);
        // Build up the expected output in a cross-platform manner.
        String nl = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(file.toString());
        sb.append(":1:1: no clucking");
        sb.append(nl);
        sb.append("cluck()");
        sb.append(nl);
        sb.append("^");
        sb.append(nl);
        // NB: We use platform encoding here, as that's what we expect the
        // formatter to produce.
        assertThat(out.toString(), is(sb.toString()));
    }

    /**
     * When there's no evidence, we shouldn't print a blank line or a caret.
     */
    @Test
    public void testNoEvidence() throws Exception {
        File file = new File("foo/bar.js");
        Issue issue = new IssueBuilder(file.toString(), 1, 1, "fatality").build();
        issues.add(issue);
        runFormatter(file);
        // Build up the expected output in a cross-platform manner.
        String nl = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder(file.toString());
        sb.append(":1:1: fatality");
        sb.append(nl);
        // NB: We use platform encoding here, as that's what we expect the
        // formatter to produce.
        assertThat(out.toString(), is(sb.toString()));
    }

}
