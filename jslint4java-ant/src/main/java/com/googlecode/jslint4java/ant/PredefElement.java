package com.googlecode.jslint4java.ant;

import org.apache.tools.ant.Project;

/**
 * An ant element to be used as a child of {@link JSLintTask}. It captures a
 * list of predefined global variable names.
 *
 * @author dom
 */
public class PredefElement {

    private final Project project;
    private final StringBuffer sb = new StringBuffer();

    public PredefElement(Project project) {
        this.project = project;
    }

    /**
     * Standard ant interface to add the text inside an element. Properties are
     * expanded.
     */
    public void addText(String s) {
        sb.append(project.replaceProperties(s));
    }

    /**
     * Return the text we've captured. This should be a comma separated list,
     * but the splitting will be handled elsewhere.
     */
    public String getText() {
        return sb.toString();
    }
}
