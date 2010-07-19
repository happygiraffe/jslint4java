package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.Issue.IssueBuilder;
import com.googlecode.jslint4java.JSLintResult;

public class PlainResultFormatterTest {

    private final PlainResultFormatter rf = new PlainResultFormatter();

    @Test
    public void testExpectedOutputNoIssues() {
        JSLintResult result = new JSLintResult.ResultBuilder("foo/bar.js").build();
        String out = rf.format(result);
        assertThat(out, is(""));
    }

    @Test
    public void testExpectedOutputOneIssue() {
        String name = "foo/bar.js";
        Issue issue = new IssueBuilder(name, 1, 1, "no clucking").evidence("cluck()").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        StringBuilder sb = new StringBuilder(name);
        sb.append(":1:1: no clucking");
        sb.append("\n");
        sb.append("cluck()");
        sb.append("\n");
        sb.append("^");
        sb.append("\n");
        assertThat(rf.format(result), is(sb.toString()));
    }

    /**
     * When there's no evidence, we shouldn't print a blank line or a caret.
     */
    @Test
    public void testNoEvidence() throws Exception {
        String name = "foo/bar.js";
        Issue issue = new IssueBuilder(name, 1, 1, "fatality").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        StringBuilder sb = new StringBuilder(name);
        sb.append(":1:1: fatality");
        sb.append("\n");
        assertThat(rf.format(result), is(sb.toString()));
    }

}
