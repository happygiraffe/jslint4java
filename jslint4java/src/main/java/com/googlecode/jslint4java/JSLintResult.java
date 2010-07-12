package com.googlecode.jslint4java;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of a JSLint run.
 *
 * @author hdm
 *
 */
public class JSLintResult {

    private final List<Function> functions = new ArrayList<Function>();
    private final List<String> globals = new ArrayList<String>();
    private final List<Identifier> implieds = new ArrayList<Identifier>();
    private final List<Issue> issues = new ArrayList<Issue>();
    private boolean json;
    private final List<String> members = new ArrayList<String>();
    private final List<Identifier> unuseds = new ArrayList<Identifier>();
    private final List<String> urls = new ArrayList<String>();

    public JSLintResult(List<Issue> issues) {
        this.issues.addAll(issues);
    }

    /** Return a list of functions defined. */
    public List<Function> getFunctions() {
        return functions;
    }

    /** List all names defined in the global namespace. */
    public List<String> getGlobals() {
        return globals;
    }

    /** List all names with implied definitions. */
    public List<Identifier> getImplieds() {
        return implieds;
    }

    /**
     * Return a list of all issues that JSLint found with this source code.
     */
    public List<Issue> getIssues() {
        return issues;
    }

    /** Don't know. Ask Doug. */
    public List<String> getMembers() {
        return members;
    }

    /** A list of unused names. */
    public List<Identifier> getUnuseds() {
        return unuseds;
    }

    /** A list of URLs encountered (when parsing HTML). */
    public List<String> getUrls() {
        return urls;
    }

    /** Was this JSON? */
    public boolean isJson() {
        return json;
    }
}
