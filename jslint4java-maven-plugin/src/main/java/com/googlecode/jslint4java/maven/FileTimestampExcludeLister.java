/**
 * 
 */
package com.googlecode.jslint4java.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;


/**
 * Produce a list of files to be excluded if they haven't been modified
 * @author danigiri
 * @see TimestampFormatter
 */
public class FileTimestampExcludeLister {

	private List<File>	files;
	private File	reportFile;

	public FileTimestampExcludeLister(List<File> files, File reportFile) {
		this.files = files;
		this.reportFile = reportFile;
	}

	/**
	* @return list of excluded files
	* @throws IOException if report file can't be read
	*////////////////////////////////////////////////////////////////////////////////
	public List<File> files() throws IOException {

		List<File> excludedFiles = new ArrayList<File>();
		
		String timestampsReport = FileUtils.fileRead(reportFile);
		String[] timestampLines = timestampsReport.split("\n");
		for (int i=0;i<timestampLines.length;i++) {
			String[] resultLineSplit = timestampLines[i].trim().split(" ");
			File sourceFile = new File(resultLineSplit[0]);
			long timestamp = Long.parseLong(resultLineSplit[1]);
			if (sourceFile.exists() && sourceFile.lastModified()==timestamp) {
				excludedFiles.add(sourceFile);
			}
		}
		return excludedFiles;
	}

}
