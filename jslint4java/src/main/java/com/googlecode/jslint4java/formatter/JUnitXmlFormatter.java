package com.googlecode.jslint4java.formatter;

import com.googlecode.jslint4java.JSLintResult;

/**
 * After a bit of experimentation, this seems to be the best way to represent
 * JSLint output in JUnit format:
 *
 * <pre>
 * <!-- All OK -->
 * &lt;testsuite failures="0" time="0.131" errors="0" skipped="0" tests="1" name="good.js">
 *   &lt;testcase time="0.022" classname="com.googlecode.jslint4java" name="good.js"/>
 * &lt;/testsuite>
 * </pre>
 *
 * <pre>
 * &lt;testsuite failures="1" time="0.2" errors="0" skipped="0" tests="1" name="bad.js">
 *   &lt;testcase time="0.078" classname="com.googlecode.jslint4java" name="bad.js">
 *     &lt;failure message="Found 11 problems" type="java.lang.AssertionError">
 * prettify.js:54:8:['PR_SHOULD_USE_CONTINUATION'] is better written in dot notation.
 * prettify.js:57:8:['PR_TAB_WIDTH'] is better written in dot notation.
 * prettify.js:63:8:['PR_normalizedHtml'] is better written in dot notation.
 * prettify.js:68:3:Bad line breaking before '='.
 * prettify.js:68:12:['PR'] is better written in dot notation.
 * prettify.js:75:3:Bad line breaking before '='.
 * prettify.js:75:12:['prettyPrintOne'] is better written in dot notation.
 * prettify.js:81:3:Bad line breaking before '='.
 * prettify.js:81:12:['prettyPrint'] is better written in dot notation.
 * prettify.js:81:29:Expected an identifier and instead saw 'void'.
 * prettify.js:81:29:Stopping, unable to continue. (5% scanned).
 *     &lt;/failure>
 *   &lt;/testcase>
 * &lt;/testsuite>
 * </pre>
 *
 * After a bit of testing in <a
 * href="http://zutubi.com/products/pulse">pulse</a> and <a
 * href="http://www.hudson-ci.org/">hudson</a>, this appears to give reasonable
 * output.
 */
public class JUnitXmlFormatter implements JSLintResultFormatter {

    public String format(JSLintResult result) {
        return "";
    }

}
