
package com.googlecode.jslint4java.maven;

import java.io.File;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;


/**
 * Output filename paths and last modification date
 * @author danigiri
 */
public class TimestampFormatter implements JSLintResultFormatter {

	
	/** No footer required. */
	public String footer() {
	    return null;
	}
	
	public String format(JSLintResult result) {
	    String nl = System.getProperty("line.separator");
	    StringBuilder sb = new StringBuilder();
	    String fileName = result.getName();
		sb.append(fileName);
	    sb.append(" ");
	    sb.append(new File(fileName).lastModified());
	    sb.append(nl);
	    return sb.toString();
	}
	
	/** No footer required. */
	public String header() {
	    return null;
	}

}
