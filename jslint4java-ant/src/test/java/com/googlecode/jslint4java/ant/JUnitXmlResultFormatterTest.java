package com.googlecode.jslint4java.ant;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Delete;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.JSLintResult;

public class JUnitXmlResultFormatterTest {

    private final JUnitXmlResultFormatter formatter = new JUnitXmlResultFormatter();
    private File tmpd;

    private JSLintResult aResult(String name) {
        return new JSLintResult.ResultBuilder(name).build();
    }

    private void formatResult(JSLintResult result) {
        formatter.begin();
        formatter.output(result);
        formatter.end();
    }

    @Before
    public void setUp() throws Exception {
        tmpd = File.createTempFile(JUnitXmlResultFormatter.class.getName() + ".", ".d");
        // I actually wanted a directory, not a file…
        if (!tmpd.delete()) {
            throw new IOException("Can't delete " + tmpd);
        }
        if (!tmpd.mkdir()) {
            throw new IOException("Can't mkdir " + tmpd);
        }
    }

    @After
    public void tearDown() throws Exception {
        // Use the ant task in order to delete a tree of files.
        Delete delete = new Delete();
        delete.setDir(tmpd);
        // I know that this produces messages on stderr, but I have no idea how
        // to tell Delete to just “STFU.” Various combinations of setVerbose()
        // and setQuiet() all fail. I guess it's just noisy. :-(
        delete.execute();
    }

    @Test(expected = BuildException.class)
    public void testFileSetReallyIsFile() throws Exception {
        File foo = new File(tmpd, "foo");
        if (!foo.createNewFile()) {
            throw new IOException("Can't create " + foo);
        }
        JSLintResult result = aResult("foo.js");
        formatter.setFile(foo);
        formatResult(result);
    }

    @Test(expected = BuildException.class)
    public void testNoFileSet() {
        formatResult(aResult("foo.js"));
    }

    @Test
    public void testNormality() {
        // File is set to a pre-existing directory.
        formatter.setFile(tmpd);
        formatResult(aResult("foo.js"));
        File expected = new File(tmpd, "TEST-foo.js.xml");
        assertTrue(expected.exists());
    }

    @Test(expected = BuildException.class)
    public void testReadOnlyFileBlowsUp() throws IOException {
        File output = new File(tmpd, "TEST-foo.js.xml");
        if (!output.createNewFile()) {
            throw new IOException("Can't create " + output);
        }
        assertTrue(output.setReadOnly());
        formatter.setFile(tmpd);
        // Should blow up when write occurs.
        formatResult(aResult("foo.js"));
    }

    @Test
    public void testNonAlphaNumericFilename() {
        formatter.setFile(tmpd);
        formatResult(aResult("a&b.js"));
        File expected = new File(tmpd, "TEST-a_b.js.xml");
        assertTrue(expected.exists());
    }

}
