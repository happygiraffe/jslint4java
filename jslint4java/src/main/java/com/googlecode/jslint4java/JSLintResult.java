package com.googlecode.jslint4java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<String, Integer> member = new HashMap<String, Integer>();
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
    public Map<String, Integer> getMember() {
        return member;
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

    void setFunctions(List<Function> functions) {
        this.functions.clear();
        this.functions.addAll(functions);
    }

    void setGlobals(List<String> globals) {
        this.globals.clear();
        this.globals.addAll(globals);
    }

    void setImplieds(List<Identifier> implieds) {
        this.implieds.clear();
        this.implieds.addAll(implieds);
    }

    void setJson(boolean json) {
        this.json = json;
    }

    void setMember(Map<String, Integer> member) {
        this.member.clear();
        this.member.putAll(member);
    }

    void setUnuseds(List<Identifier> unuseds) {
        this.unuseds.clear();
        this.unuseds.addAll(unuseds);
    }

    void setUrls(List<String> urls) {
        this.urls.clear();
        this.urls.addAll(urls);
    }
}
