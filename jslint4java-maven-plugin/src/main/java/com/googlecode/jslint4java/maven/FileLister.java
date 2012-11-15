package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Produce a list of files with includes and excludes.
 *
 * @author hdm
 * @see FileUtils#getDefaultExcludes()
 * @see FileUtils#getFiles(File, String, String)
 */
public class FileLister {

    private final List<String> includes = Lists.newArrayList();
    private final List<String> excludes = Lists.newArrayList();
    private final File sourceDirectory;

    public FileLister(File sourceDirectory, List<String> includes, List<String> excludes) {
        this.sourceDirectory = sourceDirectory;
        this.includes.addAll(includes);
        this.excludes.addAll(excludes);
        this.excludes.addAll(FileUtils.getDefaultExcludesAsList());
    }

    public List<File> files() throws IOException {
        if (!sourceDirectory.exists()) {
            return ImmutableList.of();
        }
        String includesStr = StringUtils.join(includes.iterator(), ",");
        String excludesStr = StringUtils.join(excludes.iterator(), ",");
        return FileUtils.getFiles(sourceDirectory, includesStr, excludesStr);
    }

}
