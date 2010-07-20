package com.googlecode.jslint4java.ant;

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

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

/**
 * Write out JSLint problems to an XML file. This may be easily transformed into
 * a nice report. Sample output:
 *
 * <pre>
 *  &lt;jslint&gt;
 *    &lt;file name=&quot;bad.js&quot;&gt;
 *      &lt;issue line=&quot;0&quot; char=&quot;0&quot; reason=&quot;Insufficient Llamas&quot; evidence=&quot;var sheep;&quot;/&gt;
 *    &lt;/file&gt;
 *    &lt;file name=&quot;good.js&quot;/&gt;
 *  &lt;/jslint&gt;
 * </pre>
 *
 * @author dom
 * @version $Id: XmlResultFormatter.java 141 2007-12-20 08:39:30Z
 *          happygiraffe.net $
 */
public class XmlResultFormatter implements ResultFormatter {

    private OutputStream out;
    private DocumentBuilder builder = null;
    private Document doc = null;
    private Element rootElement = null;

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
     * Write out the XML file containing the issues for all files.
     *
     * @see com.googlecode.jslint4java.ant.ResultFormatter#end()
     */
    public void end() {
        Writer w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            new DOMElementWriter().write(rootElement, w, 0, "  ");
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
     * attributes. An element will be created for all files, regardless of any
     * issues being uncovered.
     *
     * @see ResultFormatter#output(String, List)
     */
    public void output(JSLintResult result) {
        Element f = doc.createElement("file");
        f.setAttribute("name", result.getName());
        for (Issue issue : result.getIssues()) {
            f.appendChild(issueToElement(issue));
        }
        rootElement.appendChild(f);
    }

    private Element issueToElement(Issue issue) {
        Element iel = doc.createElement("issue");
        iel.setAttribute("line", Integer.toString(issue.getLine()));
        iel.setAttribute("char", Integer.toString(issue.getCharacter()));
        iel.setAttribute("reason", issue.getReason());
        iel.setAttribute("evidence", issue.getEvidence());
        return iel;
    }

    public void setOut(OutputStream os) {
        out = os;
    }

}
