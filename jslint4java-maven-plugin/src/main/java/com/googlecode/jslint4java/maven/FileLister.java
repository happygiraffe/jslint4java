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

    private final List includes;
    private final List excludes;
    private final File sourceDirectory;

    public FileLister(File sourceDirectory, List includes, List excludes) {
        this.sourceDirectory = sourceDirectory;
        this.includes = includes;
        this.excludes = excludes;
    }

    public List files() throws IOException {
        excludes.addAll(FileUtils.getDefaultExcludesAsList());

        String includesStr = StringUtils.join(includes.iterator(), ",");
        String excludesStr = StringUtils.join(excludes.iterator(), ",");
        return FileUtils.getFiles(sourceDirectory, includesStr, excludesStr);
    }

}
