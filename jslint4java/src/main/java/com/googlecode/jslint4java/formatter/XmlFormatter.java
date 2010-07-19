package com.googlecode.jslint4java.formatter;

/**
 * A convenience super-class for generating XML through a {@link StringBuilder}.
 */
public class XmlFormatter {

    protected String attr(String key, String val) {
        StringBuilder sb = new StringBuilder(' ');
        sb.append(' ');
        sb.append(escapeAttr(key));
        sb.append("='");
        sb.append(escapeAttr(val));
        sb.append("'");
        return sb.toString();
    }

    protected String escape(String str) {
        return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }

    private String escapeAttr(String str) {
        return escape(str).replaceAll("\"", "&quot;").replaceAll("\'", "&apos;");
    }

}