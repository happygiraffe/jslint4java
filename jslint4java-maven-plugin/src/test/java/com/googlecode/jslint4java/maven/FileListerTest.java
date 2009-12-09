package com.googlecode.jslint4java.maven;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class FileListerTest {

    private File sourceDirectory;

    /**
     * Check that <i>files</i> contain only the files listed in <i>names</i>.
     * Names are all relative to {@link #sourceDirectory}.
     *
     * @param files
     * @param names
     */
    private void assertFilesAre(List<File> files, String... names) {
        assertThat(files, is(notNullValue()));
        assertThat("number of files", files.size(), is(names.length));
        // Make each name into a base-relative file.
        for (String name : names) {
            // _Try_ to be portable.
            String osName = name.replace('/', File.separatorChar);
            assertThat(files, hasItem(new File(sourceDirectory, osName)));
        }
    }

    /**
     * Run {@link FileLister#files()}.
     */
    public List<File> files(List<String> includes, List<String> excludes) throws IOException {
        return new FileLister(sourceDirectory, includes, excludes).files();
    }

    // Just a convenience wrapper.
    public List<String> list(String... strings) {
        return Arrays.asList(strings);
    }

    /**
     * Look up our data directory on the classpath.
     */
    @Before
    public void setUpSourceDirectory() {
        try {
            sourceDirectory = new File(FileListerTest.class.getResource("files").toURI());
            // Not that I'm paranoid…
            assertTrue("source exists", sourceDirectory.exists());
            assertTrue("source is a directory", sourceDirectory.isDirectory());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAllFiles() throws Exception {
        List<File> files = files(list("*.txt"), list());
        assertFilesAre(files, "a.txt", "b.txt");
    }

    @Test
    public void testExcludes() throws Exception {
        List<File> files = files(list("*.txt"), list("b.txt"));
        assertFilesAre(files, "a.txt");
    }

    @Test
    public void testNoIncludesMeansNoFiles() throws Exception {
        List<File> files = files(list(), list());
        assertFilesAre(files);
    }

    @Test
    public void testRecursiveIncludes() throws Exception {
        List<File> files = files(list("**/*.txt"), list());
        assertFilesAre(files, "a.txt", "b.txt", "subdir/c.txt");
    }

    @Test
    public void testRecursiveIncludesAndExcludes() throws Exception {
        List<File> files = files(list("**/*.txt"), list("**/c.txt"));
        assertFilesAre(files, "a.txt", "b.txt");
    }

}
