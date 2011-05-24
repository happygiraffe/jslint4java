package com.googlecode.jslint4java.maven;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

// Urgh, JUnit 3. :(
public class JSLintMojoTest extends AbstractMojoTestCase {

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
        mojo.setSourceDirectory(baseRelative("good-js"));
        mojo.execute();
    }

    public void testFailure() throws Exception {
        try {
            mojo.setSourceDirectory(baseRelative("bad-js"));
            mojo.execute();
            fail("Should have one error.");
        } catch (MojoFailureException e) {
            assertEquals("JSLint found 1 problems in 1 files", e.getMessage());
        }
    }

    public void testOptions() throws Exception {
        mojo.setSourceDirectory(baseRelative("good-js"));
        Map<String,String> options = new HashMap<String, String>();
        options.put("strict", "true");
        mojo.setOptions(options);
        try {
            mojo.execute();
            fail("should have failed due to lack of 'use strict'");
        } catch (MojoFailureException e) {
            assertTrue(true);
        }
    }
}
