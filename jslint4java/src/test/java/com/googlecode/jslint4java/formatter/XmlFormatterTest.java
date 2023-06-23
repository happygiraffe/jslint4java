package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class XmlFormatterTest {

    // A subclass to enable calling the methods we're testing.
    public static class XF extends XmlFormatter {
        @Override
        public String escape(String str) {
            return super.escape(str);
        }

        @Override
        public String attr(String k, String v) {
            return super.attr(k, v);
        }

        @Override
        protected String root() {
            return "root";
        }
    }

    private final XF xf = new XF();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEscapeNull() throws Exception {
        assertThat(xf.escape(null), is(""));
    }

    @Test
    public void testEscape() throws Exception {
        assertThat(xf.escape("echo '<usage>' 2>&1"), is("echo '&lt;usage&gt;' 2&gt;&amp;1"));
    }

    @Test
    public void testAttrKeyIsNull() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("key cannot be null");
        xf.attr(null, "foo");
    }

    @Test
    public void testAttrValueIsNull() throws Exception {
        assertThat(xf.attr("foo", null), is(" foo=''"));
    }

    @Test
    public void testAttr() throws Exception {
        assertThat(xf.attr("foo", "bar"), is(" foo='bar'"));
    }

    @Test
    public void testAttrEscape() throws Exception {
        assertThat(xf.attr("foo", "\"'<&>"), is(" foo='&quot;&apos;&lt;&amp;&gt;'"));
    }
}
