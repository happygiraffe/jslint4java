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

    @Before
    public void setUp() throws IOException {
        lint = new JSLint();
    }

    // small helper function.
    private List<Issue> lint(Reader reader) throws IOException {
        return lint.lint("-", reader);
    }

    // small helper function.
    private List<Issue> lint(String source) {
        return lint.lint("-", source);
    }

    @Test
    public void testEmptySource() throws Exception {
        List<Issue> issues = lint("");
        assertThat(issues, is(not(nullValue())));
        assertThat(issues.size(), is(0));
    }

    @Test
    public void testLintReader() throws Exception {
        Reader reader = new StringReader("var foo = 1");
        List<Issue> issues = lint(reader);
        assertThat(issues, is(not(nullValue())));
        assertThat(issues.size(), is(1));
        assertThat(issues.get(0).getReason(), is("Missing semicolon."));
    }

    @Test
    public void testNoProblems() throws IOException {
        List<Issue> problems = lint("var foo = 1;");
        assertThat(problems, is(not(nullValue())));
        assertThat(problems.size(), is(0));
    }

    @Test
    public void testNullSource() throws Exception {
        List<Issue> issues = lint((String) null);
        assertThat(issues, is(not(nullValue())));
        assertThat(issues.size(), is(0));
    }

    @Test
    public void testOneProblem() throws IOException {
        // There is a missing semicolon here.
        List<Issue> problems = lint("var foo = 1");
        assertThat(problems, is(not(nullValue())));
        assertThat(problems.size(), is(1));
        assertThat(problems.get(0).getReason(), is("Missing semicolon."));
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
        lint.addOption(Option.EVIL.getInstance());
        lint.resetOptions();
        List<Issue> issues = lint(eval_js);
        assertThat(issues, is(not(nullValue())));
        assertThat(issues.size(), is(1));
        assertThat(issues.get(0).getReason(), is("eval is evil."));
    }

    @Test
    public void testSetOption() throws Exception {
        String eval_js = "eval('1');";
        // should be disallowed by default.
        List<Issue> issues = lint(eval_js);
        assertThat("evil disallowed", issues.size(), is(1));
        // Now should be a problem.
        lint.addOption(Option.EVIL.getInstance());
        issues = lint(eval_js);
        assertThat("evil allowed", issues.size(), is(0));
    }

    // http://code.google.com/p/jslint4java/issues/detail?id=1
    @Test
    public void testUnableToContinue() throws Exception {
        List<Issue> issues = lint("new Number();");
        assertThat(issues.size(), is(2));
        assertThat(issues.get(0).getReason(),
                is("'new' should not be used as a statement."));
        assertThat(issues.get(1).getReason(),
                is("Stopping, unable to continue. (0% scanned)."));
    }
}
