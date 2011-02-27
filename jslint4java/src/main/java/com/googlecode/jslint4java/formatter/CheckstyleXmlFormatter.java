package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.Issue;
import com.googlecode.jslint4java.JSLint;
import com.googlecode.jslint4java.JSLintResult;

public class CheckstyleXmlFormatter extends XmlFormatter implements JSLintResultFormatter {

    @Override
    protected String root() {
        return "checkstyle";
    }

    public String format(JSLintResult result) {
        StringBuilder sb = new StringBuilder("<file");
        sb.append(attr("name", result.getName()));
        sb.append(">\n");
        for (Issue issue : result.getIssues()) {
            sb.append("<error");
            sb.append(attr("line", Integer.toString(issue.getLine())));
            sb.append(attr("column", Integer.toString(issue.getCharacter())));
            // Based on com.puppycrawl.tools.checkstyle.api.SeverityLevel.
            sb.append(attr("severity", "warning"));
            sb.append(attr("message", issue.getReason()));
            sb.append(attr("source", JSLint.class.getName()));
            sb.append("/>\n");
        }
        sb.append("</file>");
        return sb.toString();
    }

}
