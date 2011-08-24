package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    private final List<String> includes = new ArrayList<String>();
    private final List<String> excludes = new ArrayList<String>();
    private final File sourceDirectory;

    public FileLister(File sourceDirectory, List<String> includes, List<String> excludes) {
        this.sourceDirectory = sourceDirectory;
        this.includes.addAll(includes);
        this.excludes.addAll(excludes);
        @SuppressWarnings("unchecked")
        List<String> defaultExcludes = FileUtils.getDefaultExcludesAsList();
        this.excludes.addAll(defaultExcludes);
    }

    public List<File> files() throws IOException {
        if (!sourceDirectory.exists()) {
            return new ArrayList<File>();
        }
        String includesStr = StringUtils.join(includes.iterator(), ",");
        String excludesStr = StringUtils.join(excludes.iterator(), ",");
        @SuppressWarnings("unchecked")
        List<File> files = FileUtils.getFiles(sourceDirectory, includesStr, excludesStr);
        return files;
    }

}
