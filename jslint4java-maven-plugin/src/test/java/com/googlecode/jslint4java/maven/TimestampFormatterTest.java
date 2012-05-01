/**
 * 
 */
package com.googlecode.jslint4java.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.JSLintResult.ResultBuilder;
import com.googlecode.jslint4java.formatter.PlainFormatter;


/**
 * @author danigiri
 */
public class TimestampFormatterTest {

private TimestampFormatter	timestampFormatter;

@Before
public void setUp() throws Exception {
	timestampFormatter = new TimestampFormatter();
}

private String generateTimestampFormatterLineFor(File file) {

	JSLintResult build = new JSLintResult.ResultBuilder(file.getPath()).build();
	String fileTimestampLine = timestampFormatter.format(build);
	return fileTimestampLine;
}

@Test
public void shouldEmitNullFooter() {
    assertThat(timestampFormatter.footer(), is(nullValue()));
}

@Test
public void shouldEmitNullHeader() {
    assertThat(timestampFormatter.header(), is(nullValue()));
}


/**
 * Test method for {@link com.googlecode.jslint4java.maven.TimestampFormatter#format(com.googlecode.jslint4java.JSLintResult)}.
 * @throws Exception 
 */
@Test
public void testFormat() throws Exception {
    File sourceDirectory = new File(TimestampFormatterTest.class.getResource("good-js").toURI());
    // Not that I'm paranoid…
    assertTrue("source exists", sourceDirectory.exists());
    assertTrue("source is a directory", sourceDirectory.isDirectory());
    @SuppressWarnings("unchecked")
	List<File> files = FileUtils.getFiles(sourceDirectory, "*.js", "*.txt");
	for (File file : files) {
		String fileTimestampLine = generateTimestampFormatterLineFor(file);
		String[] resultLineSplit = fileTimestampLine.trim().split(" ");
		assertThat(resultLineSplit.length, is(2));
		File formatterResultFile = new File(resultLineSplit[0]);
		long timestamp = Long.parseLong(resultLineSplit[1]);
		assertTrue("Filename on formatter result does not exist", files.contains(formatterResultFile));
		assertThat("Wrong timestamp on formatter result", formatterResultFile.lastModified(), is(timestamp));
	}
}

}
