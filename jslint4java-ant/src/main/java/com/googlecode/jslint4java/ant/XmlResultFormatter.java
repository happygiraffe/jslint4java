package com.googlecode.jslint4java.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

import com.googlecode.jslint4java.JSLintResult;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;
import com.googlecode.jslint4java.formatter.JSLintXmlFormatter;

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

    private final JSLintResultFormatter form = new JSLintXmlFormatter();
    private final StringBuilder sb = new StringBuilder();
    private OutputStream out;

    public void begin() {
        if (out == null) {
            throw new BuildException("must specify destFile for xml output");
        }
        // Clear, just in case this object gets reused.
        sb.delete(0, sb.length() - 1);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sb.append("<jslint>\n");
    }

    /**
     * Write out the XML file containing the issues for all files.
     *
     * @see com.googlecode.jslint4java.ant.ResultFormatter#end()
     */
    public void end() {
        sb.append("</jslint>");
        Writer w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(out, "UTF8"));
            w.write(sb.toString());
            w.flush();
        } catch (IOException exc) {
            throw new BuildException("Unable to write log file", exc);
        } finally {
            if (out != System.out && out != System.err) {
                FileUtils.close(w);
            }
        }
        out = null;
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
        sb.append(form.format(result));
    }

    public void setFile(File file) {
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new BuildException(e);
        }
    }

    public void setStdout(OutputStream defaultOutputStream) {
        // Ignore, we never want to write to stdout.
    }

}
