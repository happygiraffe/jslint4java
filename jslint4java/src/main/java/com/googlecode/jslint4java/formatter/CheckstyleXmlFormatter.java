package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.JSLintResult;

public class CheckstyleXmlFormatter extends XmlFormatter implements JSLintResultFormatter {

    @Override
    protected String root() {
        return "checkstyle";
    }

    public String format(JSLintResult result) {
        return null;
    }

}
