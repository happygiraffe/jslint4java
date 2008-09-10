package net.happygiraffe.jslint;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author dom
 * @version $Id$
 */
public class UtilTest {

    private final ContextFactory contextFactory = new ContextFactory();
    private final Context cx = contextFactory.enterContext();
    private ScriptableObject scope;

    @Before
    public void setUp() throws Exception {
        cx.setLanguageVersion(Context.VERSION_1_5);
        scope = cx.initStandardObjects();
    }

    @After
    public void tearDown() throws Exception {
        Context.exit();
    }

    @Test
    public void testIntValueFromJava() throws Exception {
        scope.put("foo", scope, new Integer(42));
        assertEquals(42, Util.intValue("foo", scope));
    }

    @Test
    public void testIntValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var foo = 42", "-", 1, null);
        assertEquals(42, Util.intValue("foo", scope));
    }

    @Test
    public void testIntValueNullScope() throws Exception {
        assertEquals(0, Util.intValue("foo", null));
    }

    @Test
    public void testIntValueOfUndefined() {
        assertEquals(0, Util.intValue("foo", scope));
    }

    @Test
    public void testReaderToString() throws Exception {
        StringReader reader = new StringReader("foo bar");
        assertEquals("foo bar", Util.readerToString(reader));
    }

    @Test
    public void testStringValueFromJava() throws Exception {
        scope.put("foo", scope, "bar");
        assertEquals("bar", Util.stringValue("foo", scope));
    }

    @Test
    public void testStringValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var foo = 'bar'", "-", 1, null);
        assertEquals("bar", Util.stringValue("foo", scope));
    }

    @Test
    public void testStringValueNullScope() throws Exception {
        assertEquals(null, Util.stringValue("foo", null));
    }

    @Test
    public void testStringValueOfUndefined() {
        assertEquals(null, Util.stringValue("foo", scope));
    }
}
