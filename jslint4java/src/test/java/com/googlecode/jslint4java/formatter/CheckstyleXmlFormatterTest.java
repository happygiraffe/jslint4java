package com.googlecode.jslint4java.formatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

public class CheckstyleXmlFormatterTest {

    private final JSLintResultFormatter form = new CheckstyleXmlFormatter();

    @Before
    public void setUp() {
        // This is why you need a proper testing library…
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void shouldHaveCheckstyleFooter() {
        assertThat(form.footer(), is("</checkstyle>"));
    }

    @Test
    public void shouldHaveCheckstyleHeader() {
        assertThat(form.header(), is("<checkstyle>"));
    }

    @Test
    public void shouldHaveNoProblems() throws Exception {
        JSLintResult result = new JSLintResult.ResultBuilder("hello.js").duration(0).build();
        String expected = "<file name=\"hello.js\" />";
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }

    @Test
    public void shouldHaveOneProblem() throws Exception {
        String name = "bad.js";
        Issue issue = new Issue.IssueBuilder(name, 1, 1, "this is not a daffodil").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        String expected = "<file name=\"bad.js\">"
                + "<error line='1' column='1' severity='warning' message='this is not a daffodil'"
                + " source='com.googlecode.jslint4java.JSLint' />"
                + "</file>";
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }
}
