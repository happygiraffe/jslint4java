package com.googlecode.jslint4java.maven;

import java.io.File;

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
        File pom = new File(JSLintMojoTest.class.getResource(POM_XML).toURI());
        assertNotNull(pom);
        assertTrue(pom.exists());
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
}
