package com.googlecode.jslint4java.formatter;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

public class JUnitXmlFormatterTest {

    private final JSLintResultFormatter form = new JUnitXmlFormatter();

    @Test
    public void testEscaping() throws Exception {
        String expected = "<testsuite failures=\"1\" time=\"0.000\" errors=\"1\" skipped=\"0\" "
                + "tests=\"1\" name=\"&quot;a&amp;b&apos;.js\">"
                + "<testcase time=\"0.000\" classname=\"com.googlecode.jslint4java\" "
                + "name=\"&quot;a&amp;b&apos;.js\">"
                + "<failure message=\"Found 1 problem\" type=\"java.lang.AssertionError\">"
                + "\"a&amp;b'.js:1:1:I&lt;&amp;&gt;Like&lt;angle&gt;&gt;\"brackets'\n"
                + "</failure>" + "</testcase>" + "</testsuite>";
        String name = "\"a&b\'.js";
        Issue issue = new Issue.IssueBuilder(name, 1, 1, "I<&>Like<angle>>\"brackets\'").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).duration(0).addIssue(issue)
                .build();
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }

    @Test
    public void testNoProblems() throws Exception {
        String expected = "<testsuite failures=\"0\" time=\"0.000\" errors=\"0\" skipped=\"0\" "
                + "tests=\"1\" name=\"hello.js\">"
                + "<testcase time=\"0.000\" classname=\"com.googlecode.jslint4java\" name=\"hello.js\" />"
                + "</testsuite>";
        JSLintResult result = new JSLintResult.ResultBuilder("hello.js").duration(0).build();
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }

    @Test
    public void testOneProblem() throws Exception {
        String expected = "<testsuite failures=\"1\" time=\"0.000\" errors=\"1\" skipped=\"0\" "
                + "tests=\"1\" name=\"hello.js\">"
                + "<testcase time=\"0.000\" classname=\"com.googlecode.jslint4java\" name=\"hello.js\">"
                + "<failure message=\"Found 1 problem\" type=\"java.lang.AssertionError\">"
                + "hello.js:1:1:too many aardvarks\n" + "</failure>" + "</testcase>"
                + "</testsuite>";
        String name = "hello.js";
        Issue issue = new Issue.IssueBuilder(name, 1, 1, "too many aardvarks").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).duration(0).addIssue(issue)
                .build();
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }
}
