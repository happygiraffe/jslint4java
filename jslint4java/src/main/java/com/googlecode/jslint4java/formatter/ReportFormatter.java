package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.JSLintResult;

/**
 * Emit an HTML fragment containing JSLint's report on the input.
 */
public class ReportFormatter extends XmlFormatter implements JSLintResultFormatter {

    @Override
    public String footer() {
        return "</body></html>";
    }

    public String format(JSLintResult result) {
        String name = result.getName();
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='file'>");
        sb.append("<h1");
        sb.append(attr("id", name));
        sb.append(">");
        sb.append(escape(name));
        sb.append("</h1>");
        sb.append(result.getReport());
        sb.append("</div>"); // try to fix somewhat crappy JSLint markup.
        sb.append("</div>"); // close the file div.
        return sb.toString();
    }

    @Override
    public String header() {
        return "<html><head></head><body>";
    }

    @Override
    protected String root() {
        return "html";
    }

}
