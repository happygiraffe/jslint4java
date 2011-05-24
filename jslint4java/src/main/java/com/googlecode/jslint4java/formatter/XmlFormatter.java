package com.googlecode.jslint4java.formatter;

/**
 * A convenience super-class for generating XML through a {@link StringBuilder}.
 */
public abstract class XmlFormatter {

    protected String attr(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (value == null) {
            value = "";
        }
        StringBuilder sb = new StringBuilder(' ');
        sb.append(' ');
        sb.append(escapeAttr(key));
        sb.append("='");
        sb.append(escapeAttr(value));
        sb.append("'");
        return sb.toString();
    }

    protected String escape(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;");
    }

    private String escapeAttr(String str) {
        return escape(str).replaceAll("\"", "&quot;").replaceAll("\'", "&apos;");
    }

    public String footer() {
        return "</" + root() + ">";
    }

    public String header() {
        return "<" + root() + ">";
    }

    /**
     * The name of the root element.
     */
    protected abstract String root();

}