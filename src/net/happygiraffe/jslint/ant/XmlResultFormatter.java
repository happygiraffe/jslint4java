package net.happygiraffe.jslint.ant;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.happygiraffe.jslint.Issue;

/**
 * Write out JSLint problems to an XML file. This may be easily transformed into
 * a nice report.
 *
 * @author dom
 * @version $Id$
 */
public class XmlResultFormatter implements ResultFormatter {

    private OutputStream out;
    private DocumentBuilder builder;
    private Document doc;
    private Element rootElement;

    /*
     * (non-Javadoc)
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#begin()
     */
    public void begin() {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.newDocument();
            rootElement = doc.createElement("jslint");
        } catch (ParserConfigurationException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Create a root "jslint" element.
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#end()
     */
    public void end() {
        Writer w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            (new DOMElementWriter()).write(rootElement, w, 0, "  ");
            w.flush();
        } catch (IOException exc) {
            throw new BuildException("Unable to write log file", exc);
        } finally {
            if (out != System.out && out != System.err) {
                FileUtils.close(w);
            }
        }
    }

    /**
     * Create a "file" element, containing nested "issue" elements. Each issue
     * will have <i>line</i>, <i>char</i>, <i>reason</i> and <i>evidence</i>
     * attributes.
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#output(java.util.List)
     */
    public void output(List<Issue> issues) {
        if (issues.size() == 0)
            return;

        Element file = doc.createElement("file");
        file.setAttribute("name", issues.get(0).getSystemId());
        for (Issue issue : issues) {
            file.appendChild(issueToElement(issue));
        }
        rootElement.appendChild(file);
    }

    private Element issueToElement(Issue issue) {
        Element iel = doc.createElement("issue");
        iel.setAttribute("line", Integer.toString(issue.getLine()));
        iel.setAttribute("char", Integer.toString(issue.getCharacter()));
        iel.setAttribute("reason", issue.getReason());
        iel.setAttribute("evidence", issue.getEvidence());
        return iel;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.happygiraffe.jslint.ant.ResultFormatter#setOut(java.io.OutputStream)
     */
    public void setOut(OutputStream os) {
        out = os;
    }

}
