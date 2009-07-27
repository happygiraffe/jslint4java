package com.googlecode.jslint4java.ant;

import java.util.List;
import com.googlecode.jslint4java.Issue;

/**
 * Output all JSLint errors to the console. Shows the error, the line on which
 * it occurred and a pointer to the character at which it occurred.
 *
 * @author dom
 * @version $Id$
 */
public class NetbeansResultFormatter extends PlainResultFormatter {

    /**
     * Emit all issues to the console.
     *
     * @see ResultFormatter#output(String, String, List)
     */
    @Override
    public void output(String name, String fullPath, List<Issue> issues) {
        if (issues.size() == 0)
            return;

        for (Issue issue : issues) {
	    w.println(fullPath + ":" + issue.getLine() + ": " + issue.getReason());
            w.println(issue.getEvidence());
            w.println(spaces(issue.getCharacter()) + "^");
        }
    }
}
