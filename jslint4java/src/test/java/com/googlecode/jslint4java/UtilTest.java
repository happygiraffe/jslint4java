package com.googlecode.jslint4java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.googlecode.jslint4java.Util.Converter;

/**
 * @author dom
 */
public class UtilTest {

    private final ContextFactory contextFactory = new ContextFactory();
    private final Context cx = contextFactory.enterContext();
    private ScriptableObject scope = null;

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
    public void testBooleanTrue() throws Exception {
        cx.evaluateString(scope, "var ok = true;", "-", 1, null);
        assertThat(Util.booleanValue("ok", scope), is(true));
    }

    @Test
    public void testBooleanFalse() throws Exception {
        cx.evaluateString(scope, "var ok = false;", "-", 1, null);
        assertThat(Util.booleanValue("ok", scope), is(false));
    }

    @Test
    public void testBooleanUndefined() throws Exception {
        cx.evaluateString(scope, "var ok;", "-", 1, null);
        assertThat(Util.booleanValue("ok", scope), is(false));
    }

    @Test
    public void testIntValueFromJava() throws Exception {
        scope.put("foo", scope, 42);
        assertThat(Util.intValue("foo", scope), is(42));
    }

    @Test
    public void testIntValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var foo = 42", "-", 1, null);
        assertThat(Util.intValue("foo", scope), is(42));
    }

    @Test
    public void testIntValueNullScope() throws Exception {
        assertThat(Util.intValue("foo", null), is(0));
    }

    @Test
    public void testIntValueOfUndefined() {
        cx.evaluateString(scope, "var foo;", "-", 1, null);
        assertThat(Util.intValue("foo", scope), is(0));
    }

    @Test
    public void testListValueOfObject() throws Exception {
        // Just a little helper class for the test.
        final class Foo {
            final int a;
            Foo(int a) {
                this.a = a;
            }
        }
        cx.evaluateString(scope, "var l = [{'a':1}, {'a':2}];", "-", 1, null);
        Util.Converter<Foo> c = new Converter<Foo>() {
            public Foo convert(Object obj) {
                Scriptable scope = (Scriptable) obj;
                return new Foo((int)Context.toNumber(scope.get("a", scope)));
            }
        };
        List<Foo> l = Util.listValue("l", scope, c);
        assertThat(l.size(), is(2));
        assertThat(l.get(0).a, is(1));
        assertThat(l.get(1).a, is(2));
    }

    @Test
    public void testListValueOfTypeInteger() throws Exception {
        cx.evaluateString(scope, "var l = [9,8,7];", "-", 1, null);
        List<Integer> l = Util.listValueOfType("l", Integer.class, scope);
        assertThat(l.size(), is(3));
        assertThat(l.get(0), is(9));
        assertThat(l.get(1), is(8));
        assertThat(l.get(2), is(7));
    }

    @Test
    public void testListValueOfTypeString() throws Exception {
        cx.evaluateString(scope, "var l = ['a','b','c'];", "-", 1, null);
        List<String> l = Util.listValueOfType("l", String.class, scope);
        assertThat(l.size(), is(3));
        assertThat(l.get(0), is("a"));
        assertThat(l.get(1), is("b"));
        assertThat(l.get(2), is("c"));
    }

    @Test
    public void testListValueOfNull() throws Exception {
        cx.evaluateString(scope, "var l = [null];", "-", 1, null);
        List<String> l = Util.listValueOfType("l", String.class, scope);
        assertThat(l.size(), is(1));
        assertThat(l.get(0), is(nullValue()));
    }

    @Test
    public void testListValueOfUndefined() throws Exception {
        cx.evaluateString(scope, "var undef;", "-", 1, null);
        List<String> l = Util.listValueOfType("undef", String.class, scope);
        assertThat(l.size(), is(0));
    }

    @Test
    public void testListValueWithNull() throws Exception {
        List<Void> l = Util.listValue("foo", scope, new Converter<Void>() {
            public Void convert(Object obj) {
                return null;
            }
        });
        assertThat(l, notNullValue());
        assertTrue(l.isEmpty());
    }

    @Test
    public void testReaderToString() throws Exception {
        StringReader reader = new StringReader("foo bar");
        assertThat(Util.readerToString(reader), is("foo bar"));
    }

    @Test
    public void testStringValueFromJava() throws Exception {
        scope.put("foo", scope, "bar");
        assertThat(Util.stringValue("foo", scope), is("bar"));
    }

    @Test
    public void testStringValueFromJavaScript() throws Exception {
        cx.evaluateString(scope, "var foo = 'bar'", "-", 1, null);
        assertThat(Util.stringValue("foo", scope), is("bar"));
    }

    @Test
    public void testStringValueNullScope() throws Exception {
        assertThat(Util.stringValue("foo", null), is(nullValue()));
    }

    @Test
    public void testStringValueOfUndefined() {
        cx.evaluateString(scope, "var foo;", "-", 1, null);
        assertThat(Util.stringValue("foo", scope), is(nullValue()));
    }
}
