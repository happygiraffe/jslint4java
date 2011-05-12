package com.googlecode.jslint4java.ant;

import java.util.Iterator;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

public class JSLintResultFilterer {
	
	private JSLintErrorList jsLintErrors = new JSLintErrorList();

	public JSLintResult filterResults(JSLintResult result, String[] filters) {		
		Iterator<Issue> issues = result.getIssues().iterator();
		
		while(issues.hasNext()) {
			Issue issue = issues.next();
			if (matchesFilter(issue, filters)) {
				issues.remove();
			}
		}
		return result;
	}

	private boolean matchesFilter(Issue issue, String[] filters) {
		for(String filter : filters) {
			String rawError = jsLintErrors.getErrorCodeForRawMessage(issue.getRaw());
			
			if (filter.equals("ignore_triple_equals")) {
				if (issue.getReason().contains("===") || issue.getReason().contains("!==")) {
					return true;
				}
			}
			else if (filter.equals(rawError)) {
				return true;
			}
		}
		return false;
	}

}
