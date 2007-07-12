package net.happygiraffe.jslint;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import junit.framework.TestCase;

public class IssueTest extends TestCase {

    private Context cx;
    private ScriptableObject scope;

    @Override
    protected void setUp() throws Exception {
        cx = Context.enter();
        scope = cx.initStandardObjects();
    }

    public void testToString() {
        scope.put("reason", scope, "you broke it");
        scope.put("line", scope, 1);
        scope.put("character", scope, 1);
        Issue issue = new Issue("foo.js", scope);
        assertEquals("foo.js:1:1:you broke it", issue.toString());
    }

    @Override
    protected void tearDown() throws Exception {
        Context.exit();
    }
}
