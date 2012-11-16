package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.net.URL;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.io.Resources;
import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

public class JSLintXmlFormatterTest {
    public static final String DTD_RESOURCE = "com/googlecode/jslint4java/jslint4java.dtd";

    private final JSLintResultFormatter form = new JSLintXmlFormatter();

    private Validator getValidatorFor(String xml) throws SAXException {
        URL dtd = Resources.getResource(DTD_RESOURCE);
        // Specify a validator as the documents don't have <!DOCTYPE file> in them.
        // NB: We produce a subset of the full DTD (no root jslint element), but it's enough to
        // validate.
        return new Validator(new InputSource(new StringReader(xml)), dtd.toString(), "file");
    }

    @Before
    public void setUp() {
        // This is why you need a proper testing library…
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void shouldHaveJslintFooter() {
        assertThat(form.footer(), is("</jslint>"));
    }

    @Test
    public void shouldHaveJslintHeader() {
        assertThat(form.header(), is("<jslint>"));
    }

    @Test
    public void testNoOutput() throws Exception {
        JSLintResult result = new JSLintResult.ResultBuilder("good.js").build();
        String expected = "<file name='good.js'/>";
        String actual = form.format(result);
        XMLAssert.assertXMLEqual(expected, actual);
        XMLAssert.assertXMLValid(getValidatorFor(actual));
    }

    @Test
    public void testOneIssue() throws Exception {
        String name = "bad.js";
        Issue issue = new Issue.IssueBuilder(name, 1, 1, "too many goats teleported").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        String expected = "<file name='bad.js'>"
                + "<issue line='1' char='1' reason='too many goats teleported' evidence='' />"
                + "</file>";
        String actual = form.format(result);
        XMLAssert.assertXMLEqual(expected, actual);
        XMLAssert.assertXMLValid(getValidatorFor(actual));
    }
}
