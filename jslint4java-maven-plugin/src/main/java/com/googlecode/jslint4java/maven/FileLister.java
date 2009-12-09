package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * Produce a list of files with includes and excludes.
 *
 * @author hdm
 * @see FileUtils#getDefaultExcludes()
 * @see FileUtils#getFiles(File, String, String)
 */
public class FileLister {

    private final List<String> includes;
    private final List<String> excludes;
    private final File sourceDirectory;

    public FileLister(File sourceDirectory, List<String> includes, List<String> excludes) {
        this.sourceDirectory = sourceDirectory;
        this.includes = includes;
        this.excludes = excludes;
    }

    public List<File> files() throws IOException {
        @SuppressWarnings("unchecked")
        List<String> defaultExcludes = FileUtils.getDefaultExcludesAsList();
        excludes.addAll(defaultExcludes);

        String includesStr = StringUtils.join(includes.iterator(), ",");
        String excludesStr = StringUtils.join(excludes.iterator(), ",");
        @SuppressWarnings("unchecked")
        List<File> files = FileUtils.getFiles(sourceDirectory, includesStr, excludesStr);
        return files;
    }

}
