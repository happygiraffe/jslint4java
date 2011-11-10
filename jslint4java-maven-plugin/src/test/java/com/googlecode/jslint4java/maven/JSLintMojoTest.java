package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.IOUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.googlecode.jslint4java.Option;

// NB: Even though this is being run with JUnit 4, the asserts are still largely JUnit 3.  Yay inheritance!
@RunWith(JUnit4.class)
public class JSLintMojoTest extends AbstractMojoTestCase {

    private static final String GOAL = "lint";
    /** A directory containing a file with an error in. */
    private static final String BAD_JS = "bad-js";
    /** A directory containing javascript with no lint errors. */
    private static final String GOOD_JS = "good-js";
    private static final String POM_XML = "pom.xml";

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File baseDir;
    private JSLintMojo mojo;
    private final FakeLog logger = new FakeLog();

    private void assertLogContains(String expected) {
        for (FakeLog.LogItem item : logger.loggedItems) {
            if (item.msg.toString().contains(expected)) {
                assertTrue("Found expected log message", true);
                return;
            }
        }
        fail("Didn't find error text in logs: " + expected);
    }

    /**
     * Check the file exists and is not empty.
     *
     * @return the expected file, for further inspection.
     */
    private File assertFileExists(String filename) {
        File expectedFile = new File(temp.getRoot(), filename);
        assertTrue(expectedFile + " exists", expectedFile.exists());
        assertTrue("file has non-zero length", expectedFile.length() > 0);
        return expectedFile;
    }

    private File baseRelative(String child) {
        return new File(baseDir, child);
    }

    public MojoFailureException executeMojoExpectingFailure() throws MojoExecutionException {
        try {
            mojo.execute();
            throw new AssertionFailedError("expected failure but saw none");
        } catch (MojoFailureException e) {
            return e;
        }
    }

    private File getResourceFile(String filename) throws URISyntaxException {
        URL resource = JSLintMojoTest.class.getResource(filename);
        assertNotNull("Can't find '" + filename + "' resource", resource);
        File file = new File(resource.toURI());
        assertTrue(file + " doesn't exist?", file.exists());
        return file;
    }

    private String readFile(File reportFile) throws FileNotFoundException, IOException {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(reportFile),
                    Charset.forName("UTF-8"));
            return IOUtil.toString(reader);
        } finally {
            IOUtil.close(reader);
        }
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        File pom = getResourceFile(POM_XML);
        baseDir = pom.getParentFile();
        mojo = (JSLintMojo) lookupMojo(GOAL, pom);
        mojo.setLog(logger);
        // We don't care about "use strict" for these tests.
        mojo.addOption(Option.SLOPPY, "true");
        mojo.setOutputFolder(temp.getRoot());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Given the rate that JSLint changes at this is no bad thing.
    @Test
    public void testAlternateJSLint() throws Exception {
        // This always returns "OK"
        mojo.setJslint(getResourceFile("dummy-jslint.js"));
        useBadSource();
        mojo.execute();
        // Should be no exception raised.
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
    public void testFailOnError() throws Exception {
        useBadSource();
        mojo.setFailOnError(false);
        mojo.execute();
        assertLogContains("JSLint found 1 problems in 1 files");
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
        assertLogContains("bad.js:1:26: Expected ';' and instead saw '(end)'.");
    }

    @Test
    public void testLogToFile() throws Exception {
        useBadSource();
        executeMojoExpectingFailure();
        assertFileExists("jslint.xml");
        // Additional reports we should always generate.
        assertFileExists("checkstyle.xml");
        assertFileExists("junit.xml");
        assertFileExists("report.html");
        assertFileExists("report.txt");
    }

    @Test
    public void testLogToFileContents() throws Exception {
        useGoodSource();
        mojo.execute();
        File report = assertFileExists("jslint.xml");
        Matcher m = Pattern.compile("<file\\s").matcher(readFile(report));
        assertTrue("found first <file", m.find());
        assertTrue("found second <file", m.find());
        assertFalse("no more <file", m.find());
    }

    @Test
    public void testLogToFileMakesDirectory() throws Exception {
        assertTrue(temp.getRoot().delete());
        testLogToFile();
    }

    @Test
    public void testLogToFileOnSuccess() throws Exception {
        useGoodSource();
        mojo.execute();
        assertFileExists("jslint.xml");
    }

    @Test
    public void testOptions() throws Exception {
        useGoodSource();
        Map<String, String> options = new HashMap<String, String>();
        options.put("sloppy", "false");
        mojo.setOptions(options);
        executeMojoExpectingFailure();
        assertTrue(true);
    }

    // Check the stuff we specified is actually there.
    @Test
    public void testOptionsFromPom() {
        Map<String, String> options = mojo.getOptions();
        assertEquals(2, options.size());
        assertEquals("true", options.get("undef"));
        // This actually comes from our setUp() call…
        assertEquals("true", options.get("sloppy"));
    }

    private void useBadSource() {
        mojo.setSourceFolders(Arrays.asList(baseRelative(BAD_JS)));
    }

    private void useGoodSource() {
        mojo.setSourceFolders(Arrays.asList(baseRelative(GOOD_JS)));
    }
}
