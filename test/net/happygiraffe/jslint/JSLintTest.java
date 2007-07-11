// @(#) $Id$

package net.happygiraffe.jslint;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

public class JSLintTest extends TestCase {

    private JSLint lint;

    @Override
    protected void setUp() throws Exception {
        lint = new JSLint();
    }
    
    public void testNoProblems() throws IOException {
        List<Issue> problems = lint.lint("var foo = 1;");
        assertEquals(0, problems.size());
    }

    public void testOneProblem() throws IOException {
        // There is a missing semicolon here.
        List<Issue> problems = lint.lint("var foo = 1");
        assertEquals(1, problems.size());
        Issue issue = problems.get(0);
        assertEquals("Missing semicolon.", issue.getReason());
    }
    
    public void testNullSource() throws Exception {
        List<Issue> issues = lint.lint(null);
        assertNotNull(issues);
        assertEquals(0, issues.size());
    }

    public void testEmptySource() throws Exception {
        List<Issue> issues = lint.lint("");
        assertNotNull(issues);
        assertEquals(0, issues.size());
    }
}
