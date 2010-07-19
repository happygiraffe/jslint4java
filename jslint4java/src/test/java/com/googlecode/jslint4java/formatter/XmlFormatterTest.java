package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

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
    }

    private final XF xf = new XF();

    @Test
    public void testEscapeNull() throws Exception {
        assertThat(xf.escape(null), is(""));
    }

    @Test
    public void testEscape() throws Exception {
        assertThat(xf.escape("echo '<usage>' 2>&1"), is("echo '&lt;usage>' 2>&amp;1"));
    }

    @Test(expected = NullPointerException.class)
    public void testAttrKeyIsNull() throws Exception {
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
        assertThat(xf.attr("foo", "\"'<&>"), is(" foo='&quot;&apos;&lt;&amp;>'"));
    }
}
