package net.happygiraffe.jslint;

import java.io.StringReader;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author dom
 * @version $Id$
 */
public class UtilTest extends TestCase {

    private Context cx;

    private ScriptableObject scope;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        cx = Context.enter();
        cx.setLanguageVersion(Context.VERSION_1_5);
        scope = cx.initStandardObjects();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        Context.exit();
    }

    public void testIntValueFromJava() throws Exception {
        scope.put("foo", scope, new Integer(42));
        assertEquals(42, Util.intValue("foo", scope));
    }

    public void testIntValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var foo = 42", "-", 1, null);
        assertEquals(42, Util.intValue("foo", scope));
    }

    public void testIntValueOfUndefined() {
        assertEquals(0, Util.intValue("foo", scope));
    }

    public void testStringValueFromJava() throws Exception {
        scope.put("foo", scope, "bar");
        assertEquals("bar", Util.stringValue("foo", scope));
    }

    public void testStringValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var foo = 'bar'", "-", 1, null);
        assertEquals("bar", Util.stringValue("foo", scope));
    }

    public void testStringValueOfUndefined() {
        assertEquals(null, Util.stringValue("foo", scope));
    }
    
    public void testReaderToString() throws Exception {
        StringReader reader = new StringReader("foo bar");
        assertEquals("foo bar", Util.readerToString(reader));
    }
}
