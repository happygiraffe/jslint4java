package com.googlecode.jslint4java.ant;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.googlecode.jslint4java.JSLintResult;


public class XmlResultFormatterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private final XmlResultFormatter formatter = new XmlResultFormatter();

    private void runFormatter(File output, JSLintResult result) {
        formatter.setFile(output);
        formatter.begin();
        formatter.output(result);
        formatter.end();
    }

    @Test
    public void testXmlOutput() throws Exception {
        JSLintResult result = new JSLintResult.ResultBuilder("main.js").build();
        File output = folder.newFile("report.xml");
        runFormatter(output, result);
        assertThat(output.exists(), is(true));
    }
}
