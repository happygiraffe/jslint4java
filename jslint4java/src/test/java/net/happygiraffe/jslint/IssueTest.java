package net.happygiraffe.jslint;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author dom
 * @version $Id$
 */
public class IssueTest {

    private final ContextFactory contextFactory = new ContextFactory();
    private final Context cx = contextFactory.enterContext();
    private final ScriptableObject scope = cx.initStandardObjects();

    @After
    public void tearDown() throws Exception {
        Context.exit();
    }

    @Test
    public void testEmptyError() throws Exception {
        Issue issue = new Issue("foo.js", scope);
        assertEquals(null, issue.getReason());
        assertEquals(0, issue.getLine());
        assertEquals(0, issue.getCharacter());
    }

    @Test
    public void testNullError() throws Exception {
        Issue issue = new Issue("foo.js", null);
        assertEquals(null, issue.getReason());
        assertEquals(0, issue.getLine());
        assertEquals(0, issue.getCharacter());
    }

    @Test
    public void testToString() {
        scope.put("reason", scope, "you broke it");
        scope.put("line", scope, 1);
        scope.put("character", scope, 1);
        Issue issue = new Issue("foo.js", scope);
        assertEquals("foo.js:1:1:you broke it", issue.toString());
    }
}
