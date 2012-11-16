package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.Issue.IssueBuilder;
import com.googlecode.jslint4java.JSLintResult;

public class PlainFormatterTest {

    private final PlainFormatter rf = new PlainFormatter();

    /** We don't expect this to happen, but we shouldn't blow up either. @see issue 85 */
    @Test
    public void shouldCopeWithCharacterZero() throws Exception {
        String nl = System.getProperty("line.separator");
        String name = "foo/bar.js";
        Issue issue = new IssueBuilder(name, 0, 0, "oops").evidence("BANG").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        StringBuilder sb = new StringBuilder(name);
        sb.append(":0:0: oops").append(nl);
        sb.append("BANG").append(nl);
        sb.append("^").append(nl);
        assertThat(rf.format(result), is(sb.toString()));
    }

    @Test
    public void shouldEmitNullFooter() {
        assertThat(rf.footer(), is(nullValue()));
    }

    @Test
    public void shouldEmitNullHeader() {
        assertThat(rf.header(), is(nullValue()));
    }

    @Test
    public void testExpectedOutputNoIssues() {
        JSLintResult result = new JSLintResult.ResultBuilder("foo/bar.js").build();
        String out = rf.format(result);
        assertThat(out, is(""));
    }

    @Test
    public void testExpectedOutputOneIssue() {
        String nl = System.getProperty("line.separator");
        String name = "foo/bar.js";
        Issue issue = new IssueBuilder(name, 1, 2, "no clucking").evidence("cluck()").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        StringBuilder sb = new StringBuilder(name);
        sb.append(":1:2: no clucking").append(nl);
        sb.append("cluck()").append(nl);
        sb.append(" ^").append(nl);
        assertThat(rf.format(result), is(sb.toString()));
    }

    /**
     * When there's no evidence, we shouldn't print a blank line or a caret.
     */
    @Test
    public void testNoEvidence() throws Exception {
        String nl = System.getProperty("line.separator");
        String name = "foo/bar.js";
        Issue issue = new IssueBuilder(name, 1, 1, "fatality").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        StringBuilder sb = new StringBuilder(name);
        sb.append(":1:1: fatality");
        sb.append(nl);
        assertThat(rf.format(result), is(sb.toString()));
    }

}
