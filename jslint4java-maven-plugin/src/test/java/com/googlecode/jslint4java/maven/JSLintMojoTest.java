package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
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

    private File getPom() throws URISyntaxException {
        URL pomResource = JSLintMojoTest.class.getResource(POM_XML);
        assertNotNull("Can't find '" + POM_XML + "' resource", pomResource);
        File pom = new File(pomResource.toURI());
        assertTrue(pom + " doesn't exist?", pom.exists());
        return pom;
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
        File pom = getPom();
        baseDir = pom.getParentFile();
        mojo = (JSLintMojo) lookupMojo(GOAL, pom);
        mojo.setLog(logger);
        mojo.setOutputDirectory(temp.getRoot());
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
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
        File expectedFile = new File(temp.getRoot(), "jslint.xml");
        assertTrue(expectedFile + " exists", expectedFile.exists());
        assertTrue("xml report has non-zero length", expectedFile.length() > 0);
    }

    @Test
    public void testLogToFileContents() throws Exception {
        useGoodSource();
        mojo.execute();
        File report = new File(temp.getRoot(), "jslint.xml");
        assertTrue(report + " exists", report.exists());
        Matcher m  = Pattern.compile("<file\\s").matcher(readFile(report));
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
        File expectedFile = new File(temp.getRoot(), "jslint.xml");
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
