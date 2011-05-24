package com.googlecode.jslint4java.ant;

import com.googlecode.jslint4java.formatter.CheckstyleXmlFormatter;
import com.googlecode.jslint4java.formatter.JSLintResultFormatter;

public class CheckstyleXmlResultFormatter extends XmlResultFormatter implements ResultFormatter {

    @Override
    protected JSLintResultFormatter createFormatter() {
        return new CheckstyleXmlFormatter();
    }

}
