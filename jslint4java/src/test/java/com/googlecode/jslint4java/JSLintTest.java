// @(#) $Id$

package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dom
 * @version $Id$
 */
public class JSLintTest {

    private JSLint lint = null;

    // Check that the issues list matches zero or more reasons.
    private void assertIssues(List<Issue> issues, String... reasons) {
        assertThat(issues, is(notNullValue()));
        String msg = "Actual issues: " + issues;
        assertThat(msg, issues.size(), is(reasons.length));
        for (int i = 0; i < reasons.length; i++) {
            assertThat(issues.get(i).getReason(), is(reasons[i]));
        }
    }

    // small helper function.
    private JSLintResult lint(Reader reader) throws IOException {
        return lint.lint("-", reader);
    }

    // small helper function.
    private JSLintResult lint(String source) {
        return lint.lint("-", source);
    }

    @Before
    public void setUp() throws IOException {
        lint = new JSLintBuilder().fromDefault();
    }

    @Test
    public void testAccurateColumnNumbers() {
        List<Issue> issues = lint("var foo = 1").getIssues();
        // ....................... 123456789012
        assertIssues(issues, "Missing semicolon.");
        assertThat(issues.get(0).getCharacter(), is(12));
    }

    @Test
    public void testAccurateLineNumbers() {
        List<Issue> issues = lint("var foo = 1").getIssues();
        assertIssues(issues, "Missing semicolon.");
        assertThat(issues.get(0).getLine(), is(1));
    }

    /**
     * See what information we can return about a single function.
     * @throws Exception
     */
    @Test
    public void testDataFunctions() throws Exception {
        JSLintResult result = lint("var z = 5;function foo(x) {var y = x+z;return y;}");
        assertIssues(result.getIssues());
        List<JSFunction> functions = result.getFunctions();
        assertThat(functions.size(), is(1));
        JSFunction f1 = functions.get(0);
        assertThat(f1.getName(), is("foo"));
        assertThat(f1.getLine(), is(1));
        assertThat(f1.getParams().size(), is(1));
        assertThat(f1.getParams().get(0), is("x"));
        // TODO: how to test getClosure()?
        assertThat(f1.getVars().size(), is(1));
        assertThat(f1.getVars().get(0), is("y"));
        // TODO: test getException()
        // TODO: test getOuter()
        // TODO: test getUnused()
        assertThat(f1.getGlobal().size(), is(1));
        assertThat(f1.getGlobal().get(0), is("z"));
        // TODO: test getLabel()
    }

    @Test
    public void testDataGlobals() throws Exception {
        JSLintResult result = lint("var foo = 12;");
        assertTrue(result.getIssues().isEmpty());
        assertThat(result.getGlobals(), hasItem("foo"));
    }

    @Test
    public void testDataImplieds() throws Exception {
        JSLintResult result = lint("foo();");
        assertIssues(result.getIssues());
        List<JSIdentifier> implieds = result.getImplieds();
        assertThat(implieds.size(), is(1));
        assertThat(implieds.get(0).getName(), is("foo"));
        assertThat(implieds.get(0).getLine(), is(1));
    }

    @Test
    public void testDataJsonness() throws Exception {
        JSLintResult result = lint("{\"a\":100}");
        assertIssues(result.getIssues());
        assertTrue(result.isJson());
    }

    @Test
    public void testDataMembers() throws Exception {
        JSLintResult result = lint("var obj = {\"a\":1, \"b\": 42};");
        assertIssues(result.getIssues());
        Map<String, Integer> members = result.getMember();
        assertThat(members.size(), is(2));
        // It's a count of how many times we've seen each member.
        assertThat(members.get("a"), is(1));
        assertThat(members.get("b"), is(1));
    }

    @Test
    public void testDataUnused() throws Exception {
        JSLintResult result = lint("function f() {var foo = 2;}");
        assertIssues(result.getIssues());
        List<JSIdentifier> unused = result.getUnused();
        assertThat(unused.size(), is(1));
        assertThat(unused.get(0).getName(), is("foo"));
        assertThat(unused.get(0).getLine(), is(1));
    }

    @Test
    public void testDataUrls() throws Exception {
        JSLintResult result = lint("<html><body><a href='http://example.com'>e.g.</a>"
                + "</body></html>");
        assertIssues(result.getIssues());
        List<String> urls = result.getUrls();
        assertThat(urls.size(), is(1));
        assertThat(urls.get(0), is("http://example.com"));
    }

    @Test
    public void testEmptySource() throws Exception {
        assertIssues(lint("").getIssues());
    }

    @Test
    public void testGetEdition() throws Exception {
        String edition = lint.getEdition();
        assertThat(edition, is(notNullValue()));
        String dateRe = "^\\d\\d\\d\\d-\\d\\d-\\d\\d$";
        assertTrue(edition + " matches " + dateRe, edition.matches(dateRe));
    }

    // Issue 16.
    @Test
    public void testGlobalName() throws Exception {
        String src = "/*global name: true */\nname = \"fred\";";
        assertIssues(lint(src).getIssues());
    }

    @Test
    public void testLintReader() throws Exception {
        Reader reader = new StringReader("var foo = 1");
        List<Issue> issues = lint(reader).getIssues();
        assertIssues(issues, "Missing semicolon.");
    }

    @Test
    public void testMaxErr() throws Exception {
        lint.addOption(Option.WHITE);
        lint.addOption(Option.UNDEF);
        lint.addOption(Option.MAXERR, "2");
        // Just some nasty thing I threw together. :)
        JSLintResult result = lint("if (foo=42) {\n  println(\"bother\")\n}\n");
        assertIssues(result.getIssues(), "'foo' is not defined.",
                "Expected a conditional expression and instead saw an assignment.",
                "Too many errors. (25% scanned).");
    }

    @Test
    public void testNoProblems() throws IOException {
        List<Issue> problems = lint("var foo = 1;").getIssues();
        assertIssues(problems);
    }

    @Test
    public void testNullSource() throws Exception {
        List<Issue> issues = lint((String) null).getIssues();
        assertIssues(issues);
    }

    @Test
    public void testOneProblem() throws IOException {
        // There is a missing semicolon here.
        List<Issue> problems = lint("var foo = 1").getIssues();
        assertIssues(problems, "Missing semicolon.");
    }

    /**
     * We're testing this so that we know arrays get passed into JavaScript
     * correctly.
     */
    @Test
    public void testPredefOption() throws Exception {
        lint.addOption(Option.PREDEF, "foo,bar");
        lint.addOption(Option.UNDEF);
        List<Issue> issues = lint("foo(bar(42));").getIssues();
        assertIssues(issues);
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
        List<Issue> issues = lint(eval_js).getIssues();
        assertIssues(issues, "eval is evil.");

    }

    @Test
    public void testSetOption() throws Exception {
        String eval_js = "eval('1');";
        // should be disallowed by default.
        List<Issue> issues = lint(eval_js).getIssues();
        assertIssues(issues, "eval is evil.");
        // Now should be a problem.
        lint.addOption(Option.EVIL);
        issues = lint(eval_js).getIssues();
        assertIssues(issues);
    }

    @Test
    public void testSetOptionWithArgument() throws Exception {
        // This should only pass when indent=2.
        String js = "var x = 0;\nif (true) {\n  x = 1;\n}";
        lint.addOption(Option.WHITE);
        lint.addOption(Option.INDENT, "2");
        List<Issue> issues = lint(js).getIssues();
        assertIssues(issues);
    }

    // http://code.google.com/p/jslint4java/issues/detail?id=1
    @Test
    public void testUnableToContinue() throws Exception {
        // This isn't the originally reported problem, but it tickles the
        // "can't continue" message.
        List<Issue> issues = lint("\"").getIssues();
        assertIssues(issues, "Unclosed string.", "Stopping, unable to continue. (100% scanned).");
    }
}
