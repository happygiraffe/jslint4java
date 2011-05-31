package com.googlecode.jslint4java.maven;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

// Urgh, JUnit 3. :(
public class JSLintMojoTest extends AbstractMojoTestCase {

    /** A directory containing a file with an error in. */
    private static final String BAD_JS = "bad-js";
    /** A directory containing javascript with no lint errors. */
    private static final String GOOD_JS = "good-js";
    private static final String POM_XML = "pom.xml";
    private File baseDir;
    private JSLintMojo mojo;

    private File baseRelative(String child) {
        return new File(baseDir, child);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URL pomResource = JSLintMojoTest.class.getResource(POM_XML);
        assertNotNull("Can't find '" + POM_XML + "' resource", pomResource);
        File pom = new File(pomResource.toURI());
        assertTrue(pom + " doesn't exist?", pom.exists());
        baseDir = pom.getParentFile();
        mojo = (JSLintMojo) lookupMojo("check", pom);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testBasics() throws Exception {
        useGoodSource();
        mojo.execute();
    }

    public void testDefaultEncoding() {
        assertEquals("UTF-8", mojo.getEncoding());
    }

    public void testFailure() throws Exception {
        try {
            useBadSource();
            mojo.execute();
            fail("Should have one error.");
        } catch (MojoFailureException e) {
            assertEquals("JSLint found 1 problems in 1 files", e.getMessage());
        }
    }

    public void testLogToConsole() throws Exception {
        FakeLog logger = new FakeLog();
        mojo.setLog(logger);
        useBadSource();
        try {
            mojo.execute();
            fail("should have failed");
        } catch (MojoFailureException e) {
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

    }

    public void testOptions() throws Exception {
        useGoodSource();
        Map<String, String> options = new HashMap<String, String>();
        options.put("strict", "true");
        mojo.setOptions(options);
        try {
            mojo.execute();
            fail("should have failed due to lack of 'use strict'");
        } catch (MojoFailureException e) {
            assertTrue(true);
        }
    }

    // Check the stuff we specified is actually there.
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
