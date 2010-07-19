package com.googlecode.jslint4java.formatter;

import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Test;

import com.googlecode.jslint4java.JSLintResult;

public class JUnitXmlFormatterTest {

    private final JSLintResultFormatter form = new JUnitXmlFormatter();

    @Test
    public void testNoProblems() throws Exception {
        String expected = "<testsuite failures=\"0\" time=\"0.000\" errors=\"0\" skipped=\"0\" "
                + "tests=\"1\" name=\"hello.js\">"
                + "<testcase time=\"0.000\" classname=\"com.googlecode.jslint4java\" name=\"hello.js\" />"
                + "</testsuite>";
        JSLintResult result = new JSLintResult.ResultBuilder("hello.js").duration(0).build();
        XMLAssert.assertXMLEqual(expected, form.format(result));
    }
}
