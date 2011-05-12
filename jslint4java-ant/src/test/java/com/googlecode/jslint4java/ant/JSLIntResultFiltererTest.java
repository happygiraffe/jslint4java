package com.googlecode.jslint4java.ant;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.Issue.IssueBuilder;
import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.JSLintResult.ResultBuilder;

public class JSLIntResultFiltererTest {
	private JSLintResultFilterer filterer;
	
	// test data
	private Issue weirdCondition = new IssueBuilder("systemID", 1, 1, "Weird condition.").raw("Weird condition.").build();
	private Issue strangeLoop = new IssueBuilder("systemID", 1, 1, "Strange Loop.").raw("Strange loop.").build();
	private Issue nonMatchingError = new IssueBuilder("systemID", 1, 1, "nonMatchingError.").raw("nonMatchingError").build();
	private Issue tripleEquals = new IssueBuilder("systemID", 1, 1, "Expected '===' and instead saw '=='.").raw("Expected '{a}' and instead saw '{b}'.").build();
	private Issue tripleNotEquals = new IssueBuilder("systemID", 1, 1, "Expected '!==' and instead saw '!='.").raw("Expected '{a}' and instead saw '{b}'.").build();	
	
	@Before
	public void setUp() {
		filterer = new JSLintResultFilterer();	
	}
	
	@Test
	public void filterResults_nonMatchingFilter() {
		// given
		String[] filters = new String[] { "a-filter-that-does-not-exist" };
		JSLintResult result = createResult();
		int numberOfIssues = result.getIssues().size();
				
		// when
		filterer.filterResults(result, filters);
		int filteredIssues = result.getIssues().size();
		
		// then
		assertEquals(numberOfIssues, filteredIssues);
	}
	
	@Test
	public void filterResults_matchingFilter() {
		// given
		String[] filters = new String[] { "weird_condition" };
		JSLintResult result = createResult();
				
		// when
		filterer.filterResults(result, filters);
		
		// then
		assertFalse(result.getIssues().contains(weirdCondition));
	}
	
	@Test
	public void filterResults_twoMatchingFilters() {
		// given
		String[] filters = new String[] { "weird_condition", "strange_loop" };
		JSLintResult result = createResult();
				
		// when
		filterer.filterResults(result, filters);
		
		// then
		assertFalse(result.getIssues().contains(weirdCondition));
		assertFalse(result.getIssues().contains(strangeLoop));
	}
	
	@Test
	public void filterResults_ignoreTripleEquals() {
		// given
		String[] filters = new String[] { "ignore_triple_equals", "" };
		JSLintResult result = createResult();
				
		// when
		filterer.filterResults(result, filters);
		
		// then
		assertFalse(result.getIssues().contains(tripleEquals));
		assertFalse(result.getIssues().contains(tripleNotEquals));
	}
	
	private JSLintResult createResult() {
		ResultBuilder b = new JSLintResult.ResultBuilder("systemID");
		
		b.addIssue(weirdCondition);
		b.addIssue(nonMatchingError);
		b.addIssue(strangeLoop);
		b.addIssue(tripleEquals);
		b.addIssue(tripleNotEquals);		
		return b.build();
	}	
}
