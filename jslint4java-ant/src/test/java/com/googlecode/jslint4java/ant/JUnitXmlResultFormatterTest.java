package com.googlecode.jslint4java.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.googlecode.jslint4java.JSLintResult;

public class JUnitXmlResultFormatterTest {

    private final JUnitXmlResultFormatter formatter = new JUnitXmlResultFormatter();
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private JSLintResult aResult(String name) {
        return new JSLintResult.ResultBuilder(name).build();
    }

    private void formatResult(JSLintResult result) {
        formatter.begin();
        formatter.output(result);
        formatter.end();
    }

    @Test(expected = BuildException.class)
    public void testFileSetReallyIsFile() throws Exception {
        JSLintResult result = aResult("foo.js");
        formatter.setFile(folder.newFile("foo"));
        formatResult(result);
    }

    @Test(expected = BuildException.class)
    public void testNoFileSet() {
        formatResult(aResult("foo.js"));
    }

    @Test
    public void testNormality() {
        // File is set to a pre-existing directory.
        formatter.setFile(folder.getRoot());
        formatResult(aResult("foo.js"));
        File expected = new File(folder.getRoot(), "TEST-foo.js.xml");
        assertTrue(expected.exists());
    }

    @Test(expected = BuildException.class)
    public void testReadOnlyFileBlowsUp() throws IOException {
        File output = folder.newFile("TEST-foo.js.xml");
        assertTrue(output.setReadOnly());
        formatter.setFile(folder.getRoot());
        // Should blow up when write occurs.
        formatResult(aResult("foo.js"));
    }

    @Test
    public void testNonAlphaNumericFilename() {
        formatter.setFile(folder.getRoot());
        formatResult(aResult("a&b.js"));
        File expected = new File(folder.getRoot(), "TEST-a_b.js.xml");
        assertTrue(expected.exists());
    }

}
