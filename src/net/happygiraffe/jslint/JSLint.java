package net.happygiraffe.jslint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A utility class to check JavaScript source code for potential problems.
 * 
 * @author dom
 * @version $Id$
 */
public class JSLint {

    private static final String JSLINT_FILE = "fulljslint.js";

    private Context ctx;

    private Set<Option> options = EnumSet.noneOf(Option.class);

    private ScriptableObject scope;

    /**
     * Create a new {@link JSLint} object. This reads in the jslint JavaScript
     * source as a resource.
     * 
     * @throws IOException
     *                 if something went wrong reading jslint.js.
     */
    public JSLint() throws IOException {
        ctx = Context.enter();
        scope = ctx.initStandardObjects();
        Reader reader = new BufferedReader(new InputStreamReader(getClass()
                .getResourceAsStream(JSLINT_FILE)));
        ctx.evaluateReader(scope, reader, JSLINT_FILE, 1, null);
    }

    /**
     * Add an option to change the behaviour of the lint.
     * 
     * @param o
     *                Any {@link Option}.
     */
    public void addOption(Option o) {
        options.add(o);
    }

    /**
     * Check for problems in a {@link Reader} which contains JavaScript source.
     * 
     * @param systemId
     *                a filename
     * @param reader
     *                a {@link Reader} over JavaScript source code.
     * 
     * @return a {@link List} of {@link Issue}s describing any problems.
     * @throws IOException
     */
    public List<Issue> lint(String systemId, Reader reader) throws IOException {
        return lint(systemId, Util.readerToString(reader));
    }

    /**
     * Check for problems in JavaScript source.
     * 
     * @param systemId
     *                a filename
     * @param javaScript
     *                a String of JavaScript source code.
     * 
     * @return a {@link List} of {@link Issue}s describing any problems.
     */
    public List<Issue> lint(String systemId, String javaScript) {
        String src = javaScript == null ? "" : javaScript;
        Object[] args = new Object[] { src, optionsAsJavaScriptObject() };
        Function lintFunc = (Function) scope.get("JSLINT", scope);
        Boolean ok = (Boolean) lintFunc.call(ctx, scope, scope, args);
        List<Issue> issues = new ArrayList<Issue>();
        if (!ok.booleanValue()) {
            readErrors(systemId, issues);
        }
        return issues;
    }

    /**
     * Turn the set of options into a JavaScript object, where the key is the
     * name of the option and the value is true.
     */
    private Scriptable optionsAsJavaScriptObject() {
        Scriptable opts = ctx.newObject(scope);
        for (Option o : options) {
            opts.put(o.getLowerName(), opts, true);
        }
        return opts;
    }

    private void readErrors(String systemId, List<Issue> issues) {
        Scriptable JSLINT = (Scriptable) scope.get("JSLINT", scope);
        Scriptable errors = (Scriptable) JSLINT.get("errors", JSLINT);
        int count = Util.intValue("length", errors);
        for (int i = 0; i < count; i++) {
            Scriptable err = (Scriptable) errors.get(i, errors);
            issues.add(new Issue(systemId, err));
        }
    }

    /**
     * Clear out all options that have been set with {@link #addOption(Option)}.
     */
    public void resetOptions() {
        options = EnumSet.noneOf(Option.class);
    }

}
