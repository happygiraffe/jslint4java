// @(#) $Id$

package net.happygiraffe.jslint;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dom
 * @version $Id$
 */
public class JSLintTest {

    private JSLint lint;

    // Check that the issues list matches zero or more reasons.
    private void assertIssues(List<Issue> issues, String... reasons) {
        assertThat(issues, is(notNullValue()));
        assertThat(issues.size(), is(reasons.length));
        for (int i = 0; i < reasons.length; i++) {
            assertThat(issues.get(i).getReason(), is(reasons[i]));
        }
    }

    // small helper function.
    private List<Issue> lint(Reader reader) throws IOException {
        return lint.lint("-", reader);
    }

    // small helper function.
    private List<Issue> lint(String source) {
        return lint.lint("-", source);
    }

    @Before
    public void setUp() throws IOException {
        lint = new JSLint();
    }

    @Test
    public void testEmptySource() throws Exception {
        List<Issue> issues = lint("");
        assertIssues(issues);
    }

    @Test
    public void testLintReader() throws Exception {
        Reader reader = new StringReader("var foo = 1");
        List<Issue> issues = lint(reader);
        assertIssues(issues, "Missing semicolon.");
    }

    @Test
    public void testNoProblems() throws IOException {
        List<Issue> problems = lint("var foo = 1;");
        assertIssues(problems);
    }

    @Test
    public void testNullSource() throws Exception {
        List<Issue> issues = lint((String) null);
        assertIssues(issues);
    }

    @Test
    public void testOneProblem() throws IOException {
        // There is a missing semicolon here.
        List<Issue> problems = lint("var foo = 1");
        assertIssues(problems, "Missing semicolon.");
    }

    @Test
    public void testReportErrorsOnly() throws Exception {
        String html = lint.report("var foo = 42", true);
        assertThat(html, containsString("<div id=errors"));
        assertThat(html, containsString("Missing semicolon"));
    }

    @Test
    public void testReportFull() throws Exception {
        String html = lint.report("var foo = 42;");
        assertThat(html, containsString("<div>"));
    }

    @Test
    public void testResetOptions() throws Exception {
        String eval_js = "eval('1');";
        lint.addOption(Option.EVIL);
        lint.resetOptions();
        List<Issue> issues = lint(eval_js);
        assertIssues(issues, "eval is evil.");

    }

    @Test
    public void testSetOption() throws Exception {
        String eval_js = "eval('1');";
        // should be disallowed by default.
        List<Issue> issues = lint(eval_js);
        assertIssues(issues, "eval is evil.");
        // Now should be a problem.
        lint.addOption(Option.EVIL);
        issues = lint(eval_js);
        assertIssues(issues);
    }

    @Test
    public void testSetOptionWithArgument() throws Exception {
        // This should only pass when indent=2.
        String js = "var x = 0;\nif (true) {\n  x = 1;\n}";
        lint.addOption(Option.WHITE);
        lint.addOption(Option.INDENT, 2);
        List<Issue> issues = lint(js);
        assertIssues(issues);
    }

    // http://code.google.com/p/jslint4java/issues/detail?id=1
    @Test
    public void testUnableToContinue() throws Exception {
        // This isn't the originally reported problem, but it tickles the
        // "can't continue" message.
        List<Issue> issues = lint("\"");
        assertIssues(issues, "Unclosed string.",
                "Stopping, unable to continue. (0% scanned).");
    }
}
