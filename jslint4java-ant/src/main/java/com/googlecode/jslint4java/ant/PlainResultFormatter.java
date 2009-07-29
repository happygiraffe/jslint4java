package com.googlecode.jslint4java.ant;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import org.apache.tools.ant.util.FileUtils;

import com.googlecode.jslint4java.Issue;

/**
 * Output all JSLint errors to the console. Shows the error, the line on which
 * it occurred and a pointer to the character at which it occurred.
 *
 * @author dom
 * @version $Id$
 */
public class PlainResultFormatter implements ResultFormatter {

    protected OutputStream out;
    protected PrintWriter w = null;

    public void begin() {
        // Use the default system encoding, as that's likely what the console is
        // set to...
        w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
    }

    public void end() {
        FileUtils.close(w);
        w = null;
    }

    /**
     * Emit all issues to the console.
     *
     * @see ResultFormatter#output(String, List)
     */
    public void output(String name, List<Issue> issues) {
        if (issues.size() == 0)
            return;

        for (Issue issue : issues) {
            w.println(issue.toString());
            w.println(issue.getEvidence());
            // character is now one-based.
            w.println(spaces(issue.getCharacter() - 1) + "^");
        }
    }

    public void setOut(OutputStream os) {
        out = os;
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
