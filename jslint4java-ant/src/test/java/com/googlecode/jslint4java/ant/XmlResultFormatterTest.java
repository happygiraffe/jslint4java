package com.googlecode.jslint4java.ant;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

public class XmlResultFormatterTest {
    public static final String DTD_RESOURCE = "com/googlecode/jslint4java/jslint4java.dtd";
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final XmlResultFormatter formatter = new XmlResultFormatter();

    private void assertValid(File output) throws FileNotFoundException, SAXException {
        InputSource xml = new InputSource(new FileInputStream(output));
        URL dtdUrl = getClass().getClassLoader().getResource(DTD_RESOURCE);
        assertThat("resource " + DTD_RESOURCE + " exists", dtdUrl, notNullValue());
        // Specify a validator as the documents don't have <!DOCTYPE jslint> in them.
        Validator validator = new Validator(xml, dtdUrl.toString(), "jslint");
        assertThat(validator.toString(), validator.isValid(), is(true));
    }

    private void runFormatter(File output, JSLintResult result) {
        formatter.setFile(output);
        formatter.begin();
        formatter.output(result);
        formatter.end();
    }

    private void runTest(JSLintResult result) throws IOException, FileNotFoundException,
            SAXException {
        File output = folder.newFile("report.xml");
        runFormatter(output, result);
        assertThat(output.exists(), is(true));
        assertValid(output);
    }

    @Before
    public void setUp() {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testXmlOutputBad() throws Exception {
        String name = "main.js";
        Issue issue = new Issue.IssueBuilder(name, 1, 1, "smelly socks").build();
        JSLintResult result = new JSLintResult.ResultBuilder(name).addIssue(issue).build();
        runTest(result);
    }

    @Test
    public void testXmlOutputGood() throws Exception {
        JSLintResult result = new JSLintResult.ResultBuilder("main.js").build();
        runTest(result);
    }
}
