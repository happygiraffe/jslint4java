package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class JSLintMojoTest extends AbstractMojoTestCase {

    private static final String GOAL = "lint";
    /** A directory containing a file with an error in. */
    private static final String BAD_JS = "bad-js";
    /** A directory containing javascript with no lint errors. */
    private static final String GOOD_JS = "good-js";
    private static final String POM_XML = "pom.xml";
    private File baseDir;
    private JSLintMojo mojo;
    private final FakeLog logger = new FakeLog();
    private File tempDir;

    private File baseRelative(String child) {
        return new File(baseDir, child);
    }

    /**
     * Delete recursively.
     *
     * @see {@code http://stackoverflow.com/questions/779519/delete-files-recursively-in-java/779529#779529}
     */
    private void delete(File f) throws FileNotFoundException {
        if (f.isDirectory()) {
            for (File kid : f.listFiles()) {
                delete(kid);
            }
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file " + f);
        }
    }

    public MojoFailureException executeMojoExpectingFailure() throws MojoExecutionException {
        try {
            mojo.execute();
            throw new AssertionFailedError("expected failure but saw none");
        } catch (MojoFailureException e) {
            return e;
        }
    }

    private File getPom() throws URISyntaxException {
        URL pomResource = JSLintMojoTest.class.getResource(POM_XML);
        assertNotNull("Can't find '" + POM_XML + "' resource", pomResource);
        File pom = new File(pomResource.toURI());
        assertTrue(pom + " doesn't exist?", pom.exists());
        return pom;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setUpTempDir();
        File pom = getPom();
        baseDir = pom.getParentFile();
        mojo = (JSLintMojo) lookupMojo(GOAL, pom);
        mojo.setLog(logger);
        mojo.setOutputDirectory(tempDir);
    }

    private void setUpTempDir() throws IOException {
        tempDir = File.createTempFile(getClass().getName() + "-", "");
        tempDir.delete();
        tempDir.mkdir();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        delete(tempDir);
    }

    @Test
    public void testBasics() throws Exception {
        useGoodSource();
        mojo.execute();
    }

    @Test
    public void testDefaultEncoding() {
        assertEquals("UTF-8", mojo.getEncoding());
    }

    @Test
    public void testFailure() throws Exception {
        useBadSource();
        MojoFailureException e = executeMojoExpectingFailure();
        assertEquals("JSLint found 1 problems in 1 files", e.getMessage());
    }

    @Test
    public void testLogToConsole() throws Exception {
        useBadSource();
        executeMojoExpectingFailure();
        assertTrue("we logged something", !logger.loggedItems.isEmpty());
        String expected = "bad.js:1:26: Expected ';' and instead saw '(end)'.";
        for (FakeLog.LogItem item : logger.loggedItems) {
            if (item.msg.toString().contains(expected)) {
                assertTrue("Found expected log message", true);
                return;
            }
        }
        fail("Didn't find error text in logs: " + expected);
    }

    @Test
    public void testLogToFile() throws Exception {
        useBadSource();
        executeMojoExpectingFailure();
        File expectedFile = new File(tempDir, "jslint.xml");
        assertTrue(expectedFile + " exists", expectedFile.exists());
        assertTrue("xml report has non-zero length", expectedFile.length() > 0);
    }

    @Test
    public void testLogToFileMakesDirectory() throws Exception {
        assertTrue(tempDir.delete());
        testLogToFile();
    }

    @Test
    public void testLogToFileOnSuccess() throws Exception {
        useGoodSource();
        mojo.execute();
        File expectedFile = new File(tempDir, "jslint.xml");
        assertTrue(expectedFile + " exists", expectedFile.exists());
        assertTrue("xml report has non-zero length", expectedFile.length() > 0);
    }

    @Test
    public void testOptions() throws Exception {
        useGoodSource();
        Map<String, String> options = new HashMap<String, String>();
        options.put("strict", "true");
        mojo.setOptions(options);
        executeMojoExpectingFailure();
        assertTrue(true);
    }

    // Check the stuff we specified is actually there.
    @Test
    public void testOptionsFromPom() {
        Map<String, String> options = mojo.getOptions();
        assertEquals(1, options.size());
        assertEquals("true", options.get("undef"));
    }

    private void useBadSource() {
        mojo.setSourceDirectory(baseRelative(BAD_JS));
    }

    private void useGoodSource() {
        mojo.setSourceDirectory(baseRelative(GOOD_JS));
    }
}
