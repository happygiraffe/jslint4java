package net.happygiraffe.jslint.ant;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import net.happygiraffe.jslint.Issue;

import org.apache.tools.ant.util.FileUtils;

/**
 * @author dom
 * @version $Id$
 */
public class PlainResultFormatter implements ResultFormatter {

    private OutputStream out;
    private PrintWriter w;

    /*
     * (non-Javadoc)
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#begin()
     */
    public void begin() {
        w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
    }

    /*
     * (non-Javadoc)
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#end()
     */
    public void end() {
        FileUtils.close(w);
        w = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#output(java.util.List)
     */
    public void output(List<Issue> issues) {
        if (issues.size() == 0)
            return;

        for (Issue issue : issues) {
            w.println(issue.toString());
            w.println(issue.getEvidence());
            w.println(spaces(issue.getCharacter()) + "^");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#setOut(java.io.OutputStream)
     */
    public void setOut(OutputStream os) {
        out = os;
    }

    /**
     * Return a string of <i>howmany</i> spaces.
     *
     * @param howmany
     * @return
     */
    private String spaces(int howmany) {
        StringBuffer sb = new StringBuffer(howmany);
        for (int i = 0; i < howmany; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
