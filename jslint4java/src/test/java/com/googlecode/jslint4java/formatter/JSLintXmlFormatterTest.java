package com.googlecode.jslint4java.formatter;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

public class JSLintXmlFormatterTest {

    private final JSLintResultFormatter form = new JSLintXmlFormatter();

    @Before
    public void setUp() {
        // This is why you need a proper testing library…
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testNoOutput() throws Exception {
        JSLintResult result = new JSLintResult.ResultBuilder("good.js").build();
        String expected = "<file name='good.js'/>";
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }

    @Test
    public void testOneIssue() throws Exception {
        String name = "bad.js";
        Issue issue = new Issue.IssueBuilder(name, 1, 1, "too many goats teleported").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        String expected = "<file name='bad.js'>"
                + "<issue line='1' char='1' reason='too many goats teleported' evidence='' />"
                + "</file>";
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }
}
