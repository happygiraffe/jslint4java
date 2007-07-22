package net.happygiraffe.jslint;

import java.io.StringReader;
import java.util.EnumSet;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import junit.framework.TestCase;

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

    public void testBooleanValueFromJava() throws Exception {
        scope.put("ok", scope, new Boolean(true));
        assertEquals(true, Util.booleanValue("ok", scope));
    }

    public void testBooleanValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var ok = true", "-", 1, null);
        assertEquals(true, Util.booleanValue("ok", scope));
    }

    public void testBooleanValueOfUndefined() throws Exception {
        assertEquals(false, Util.booleanValue("ok", scope));
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

    public void testOptionSetToObjectLiteralWithNoOptions() throws Exception {
        Set<Option> opts = EnumSet.noneOf(Option.class);
        assertEquals("{}", Util.optionSetToObjectLiteral(opts));
    }

    public void testOptionSetToObjectLiteralWithOneOption() throws Exception {
        Set<Option> opts = EnumSet.of(Option.EVIL);
        assertEquals("{evil:true}", Util.optionSetToObjectLiteral(opts));
    }

    public void testOptionSetToObjectLiteralWithTwoOptions() throws Exception {
        Set<Option> opts = EnumSet.of(Option.EVIL, Option.ADSAFE);
        // NB: This test may break if not using EnumSet. That guarantees that
        // everything comes out in ordinal order, which we defined as
        // alphabetic...
        assertEquals("{adsafe:true,evil:true}", Util
                .optionSetToObjectLiteral(opts));
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
