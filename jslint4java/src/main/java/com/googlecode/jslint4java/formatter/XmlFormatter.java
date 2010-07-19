package com.googlecode.jslint4java.formatter;

/**
 * A convenience super-class for generating XML through a {@link StringBuilder}.
 */
public class XmlFormatter {

    protected void attr(StringBuilder sb, String key, String val) {
        sb.append(' ');
        sb.append(escapeAttr(key));
        sb.append("='");
        sb.append(escapeAttr(val));
        sb.append("'");
    }

    protected String escape(String str) {
        return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }

    private String escapeAttr(String str) {
        return escape(str).replaceAll("\"", "&quot;").replaceAll("\'", "&apos;");
    }

}