package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.JSLintResult;

public class ReportFormatterTest {

    private final ReportFormatter form = new ReportFormatter();

    private JSLintResult mockResult(String name) {
        String report = "<div>undefined cat: schrodinger";
        return new JSLintResult.ResultBuilder(name).report(report).build();
    }

    @Before
    public void setUp() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testEscaping() throws Exception {
        JSLintResult result = mockResult("'a<b&c>d\".js");
        String expected = "<h1 id='&apos;a&lt;b&amp;c>d&quot;.js'>'a&lt;b&amp;c>d\".js</h1>";
        assertThat(form.format(result).contains(expected), is(true));
    }

    @Test
    public void testOutput() throws Exception {
        // Normally, JSLint produces non-xml reports (though they are HTML). But as we control the
        // input we can get away with using xmlunit to make the check for us. We still have to
        // accommodate the extra div that we insert.
        JSLintResult result = mockResult("foo.js");
        String expected = "<div class='file'>" + "<h1 id='foo.js'>foo.js</h1>"
                + "<div>undefined cat: schrodinger</div>" + "</div>";
        XMLUnit.compareXML(expected, form.format(result));
    }
}
