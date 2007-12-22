package net.happygiraffe.jslint.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import net.happygiraffe.jslint.Issue;

import org.apache.tools.ant.util.FileUtils;

/**
 * Output all JSLint errors to the console. Shows the error, the line on which
 * it occurred and a pointer to the character at which it occurred.
 *
 * @author dom
 * @version $Id$
 */
public class PlainResultFormatter implements ResultFormatter {

    private OutputStream out;
    private PrintWriter w;

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
     * @see ResultFormatter#output(File, List)
     */
    public void output(File file, List<Issue> issues) {
        if (issues.size() == 0)
            return;

        for (Issue issue : issues) {
            w.println(issue.toString());
            w.println(issue.getEvidence());
            w.println(spaces(issue.getCharacter()) + "^");
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
    private String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
