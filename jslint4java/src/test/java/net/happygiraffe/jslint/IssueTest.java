package net.happygiraffe.jslint;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import junit.framework.TestCase;

/**
 * @author dom
 * @version $Id$
 */
public class IssueTest extends TestCase {

    private Context cx;
    private ScriptableObject scope;

    @Override
    protected void setUp() throws Exception {
        cx = Context.enter();
        scope = cx.initStandardObjects();
    }

    @Override
    protected void tearDown() throws Exception {
        Context.exit();
    }

    public void testEmptyError() throws Exception {
        Issue issue = new Issue("foo.js", scope);
        assertEquals(null, issue.getReason());
        assertEquals(0, issue.getLine());
        assertEquals(0, issue.getCharacter());
    }

    public void testNullError() throws Exception {
        Issue issue = new Issue("foo.js", null);
        assertEquals(null, issue.getReason());
        assertEquals(0, issue.getLine());
        assertEquals(0, issue.getCharacter());
    }

    public void testToString() {
        scope.put("reason", scope, "you broke it");
        scope.put("line", scope, 1);
        scope.put("character", scope, 1);
        Issue issue = new Issue("foo.js", scope);
        assertEquals("foo.js:1:1:you broke it", issue.toString());
    }
}
