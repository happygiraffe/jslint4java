package com.googlecode.jslint4java.formatter;

import java.util.List;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLintResult;

/**
 * Convert a single {@link JSLintResult} into an XML representation.
 *
 * <pre>
 *    &lt;file name=&quot;bad.js&quot;&gt;
 *      &lt;issue line=&quot;0&quot; char=&quot;0&quot; reason=&quot;Insufficient Llamas&quot; evidence=&quot;var sheep;&quot;/&gt;
 *    &lt;/file&gt;
 *    &lt;file name=&quot;good.js&quot;/&gt;
 * </pre>
 *
 * @author dom
 */
public class JSLintXmlFormatter extends XmlFormatter implements JSLintResultFormatter {

    public String format(JSLintResult result) {
        StringBuilder sb = new StringBuilder("<file");
        sb.append(attr("name", result.getName()));
        sb.append(">\n");
        List<Issue> issues = result.getIssues();
        for (Issue issue : issues) {
            sb.append("<issue");
            sb.append(attr("line", Integer.toString(issue.getLine())));
            sb.append(attr("char", Integer.toString(issue.getCharacter())));
            sb.append(attr("reason", issue.getReason()));
            sb.append(attr("evidence", issue.getEvidence()));
            sb.append("/>\n");
        }
        sb.append("</file>");
        return sb.toString();
    }

    @Override
    protected String root() {
        return "jslint";
    }

}
