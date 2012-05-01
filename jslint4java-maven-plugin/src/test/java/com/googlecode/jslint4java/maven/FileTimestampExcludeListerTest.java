/**
 * 
 */
package com.googlecode.jslint4java.maven;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.googlecode.jslint4java.JSLintResult;


/**
 * @author danigiri
 */
public class FileTimestampExcludeListerTest {

	@Rule
	public TemporaryFolder tmpf = new TemporaryFolder();
	
    private File sourceCopyDirectory;
	private File	reportFile;
	private List<File> sourceFiles;
	
	private void generateReportFrom(List<File> files,File reportFile) {
		
		ReportWriterImpl reportWriter = new ReportWriterImpl(reportFile, new TimestampFormatter());
		try{
			reportWriter.open();
			for (File f : files) {
				JSLintResult result = new JSLintResult.ResultBuilder(f.getPath()).build();
				reportWriter.report(result);
			}
		} catch (Exception e) {
			fail("This report should work"+e.getMessage());
		} finally {
			reportWriter.close();
		}
		assertTrue("Report exists (paranoid)",reportFile.exists());
		
	}

	
	private List<File> getExcludedFilesFromReport() throws IOException {
	
		FileTimestampExcludeLister excludeLister = new FileTimestampExcludeLister(sourceFiles,reportFile);
		List<File> excludedFiles = excludeLister.files();
		return excludedFiles;
		
	}


	@Before
	public void setUpSourceDirectory() throws Exception {

		try {
	        File sourceDirectory = new File(FileTimestampExcludeListerTest.class.getResource("good-js").toURI());
	        // Not that I'm paranoid…
	        assertTrue("source exists", sourceDirectory.exists());
	        assertTrue("source is a directory", sourceDirectory.isDirectory());
	        sourceCopyDirectory = tmpf.newFolder();
	        FileUtils.copyDirectory(sourceDirectory, sourceCopyDirectory);
			File reportDirectory = tmpf.newFolder();
			reportFile = new File(reportDirectory,"timestamps.txt");
			sourceFiles = FileUtils.getFiles(sourceCopyDirectory, "*.js", "*.txt");
			generateReportFrom(sourceFiles, reportFile);

	    } catch (URISyntaxException e) {
	        throw new RuntimeException(e);
	    }
		
	}


	@Test
	public void testExcludeAll() throws Exception {
		
		// should exclude all
		List<File> excludedFiles = getExcludedFilesFromReport();
		assertThat("excluded count",excludedFiles.size(),is(sourceFiles.size()));
		
	}

	
	@Test
	public void testExcludeNone() throws Exception {

		Thread.sleep(1001);	// wait one second so timestamp actually changes

		for (File file : sourceFiles) {
			FileUtils.fileAppend(file.getPath(), " ");
		}
		
		List<File> excludedFiles = getExcludedFilesFromReport();
		assertThat("excluded count", excludedFiles.size(), is(0));
		
	}
	
	
	@Test
	public void testExcludeOne() throws Exception {

		Thread.sleep(1001);	// wait one second so timestamp actually changes
	
		File modifiedFile = sourceFiles.get(0);
		FileUtils.fileAppend(modifiedFile.getPath(), " ");
		List<File> excludedFiles = getExcludedFilesFromReport();
		assertEquals("excluded count", 1, excludedFiles.size());
		assertThat("not exclude modified", excludedFiles, not(hasItem(modifiedFile)));
		
	}
	
	
	@Test(expected=IOException.class)
	public void testReportFileDoesNotExist() throws IOException {
		
		FileTimestampExcludeLister excludeLister = new FileTimestampExcludeLister(sourceFiles,new File("doesntexist"));
		excludeLister.files();
		fail("exception is not thrown");
	}
	
}
