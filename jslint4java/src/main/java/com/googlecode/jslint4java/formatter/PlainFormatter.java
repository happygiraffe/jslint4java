package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

/**
 * Output all JSLint errors to the console. Shows the error, the line on which it occurred and a
 * pointer to the character at which it occurred.
 *
 * @author dom
 */
public class PlainFormatter implements JSLintResultFormatter {

    /** No footer required. */
    public String footer() {
        return null;
    }

    public String format(JSLintResult result) {
        StringBuilder sb = new StringBuilder();
        for (Issue issue : result.getIssues()) {
            sb.append(outputOneIssue(issue));
        }
        return sb.toString();
    }

    /** No footer required. */
    public String header() {
        return null;
    }

    private String outputOneIssue(Issue issue) {
        StringBuilder sb = new StringBuilder();
        sb.append(issue.getSystemId());
        sb.append(':');
        sb.append(issue.getLine());
        sb.append(':');
        sb.append(issue.getCharacter());
        // NB: space before reason to look like javac!
        sb.append(": ");
        sb.append(issue.getReason());
        sb.append("\n");
        String evidence = issue.getEvidence();
        if (evidence != null && !"".equals(evidence)) {
            sb.append(evidence);
            sb.append("\n");
            // character is now one-based.
            sb.append(spaces(issue.getCharacter() - 1));
            sb.append("^\n");
        }
        return sb.toString();
    }

    /**
     * Return a string of <i>howmany</i> spaces.
     *
     * @param howmany
     */
    protected String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
