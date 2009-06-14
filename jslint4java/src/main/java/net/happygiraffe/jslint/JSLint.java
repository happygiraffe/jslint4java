package net.happygiraffe.jslint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
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

    // Uncomment to enable the rhino debugger.
    // static {
    // org.mozilla.javascript.tools.debugger.Main.mainEmbedded(null);
    // }

    private static ContextFactory contextFactory = new ContextFactory();

    private final Map<Option, Object> options = new EnumMap<Option, Object>(Option.class);

    private final ScriptableObject scope;

    /**
     * Create a new {@link JSLint} object. This reads in the jslint JavaScript
     * source as a resource.
     *
     * @throws IOException
     *             if something went wrong reading jslint.js.
     */
    public JSLint() throws IOException {
        Context ctx = contextFactory.enterContext();
        scope = ctx.initStandardObjects();
        Reader reader = new BufferedReader(new InputStreamReader(JSLint.class
                .getResourceAsStream(JSLINT_FILE)));
        ctx.evaluateReader(scope, reader, JSLINT_FILE, 1, null);
    }

    /**
     * Add an option to change the behaviour of the lint.
     *
     * @param o
     *            Any {@link Option}.
     */
    public void addOption(Option o) {
        options.put(o, Boolean.TRUE);
    }

    private void doLint(String javaScript) {
        String src = javaScript == null ? "" : javaScript;
        Object[] args = new Object[] { src, optionsAsJavaScriptObject() };
        Function lintFunc = (Function) scope.get("JSLINT", scope);
        // JSLINT actually returns a boolean, but we ignore it as we always go
        // and look at the errors in more detail.
        lintFunc.call(Context.getCurrentContext(), scope, scope, args);
    }

    /**
     * Check for problems in a {@link Reader} which contains JavaScript source.
     *
     * @param systemId
     *            a filename
     * @param reader
     *            a {@link Reader} over JavaScript source code.
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
     *            a filename
     * @param javaScript
     *            a String of JavaScript source code.
     *
     * @return a {@link List} of {@link Issue}s describing any problems.
     */
    public List<Issue> lint(String systemId, String javaScript) {
        doLint(javaScript);
        List<Issue> issues = new ArrayList<Issue>();
        readErrors(systemId, issues);
        return issues;
    }

    /**
     * Turn the set of options into a JavaScript object, where the key is the
     * name of the option and the value is true.
     */
    private Scriptable optionsAsJavaScriptObject() {
        Scriptable opts = Context.getCurrentContext().newObject(scope);
        for (Entry<Option, Object> entry : options.entrySet()) {
            String key = entry.getKey().getLowerName();
            Object value = entry.getValue();
            opts.put(key, opts, value);
        }
        return opts;
    }

    private void readErrors(String systemId, List<Issue> issues) {
        Scriptable JSLINT = (Scriptable) scope.get("JSLINT", scope);
        Scriptable errors = (Scriptable) JSLINT.get("errors", JSLINT);
        int count = Util.intValue("length", errors);
        for (int i = 0; i < count; i++) {
            Scriptable err = (Scriptable) errors.get(i, errors);
            // JSLINT spits out a null when it cannot proceed.
            // TODO Should probably turn i-1th issue into a "fatal".
            if (err != null) {
                issues.add(new Issue(systemId, err));
            }
        }
    }

    /**
     * Report on what variables / functions are in use by this code.
     *
     * @param javaScript
     * @return an HTML report.
     */
    public String report(String javaScript) {
        return report(javaScript, false);
    }

    /**
     * Report on what variables / functions are in use by this code.
     *
     * @param javaScript
     * @param errorsOnly
     *            If a report consisting solely of the problems is desired.
     * @return an HTML report.
     */
    public String report(String javaScript, boolean errorsOnly) {
        // Run the lint function itself as prep.
        doLint(javaScript);

        // The run the reporter.
        Object[] args = new Object[] { Boolean.valueOf(errorsOnly) };
        Scriptable lintScope = (Scriptable) scope.get("JSLINT", scope);
        Function reportFunc = (Function) lintScope.get("report", lintScope);
        // JSLINT actually returns a boolean, but we ignore it as we always go
        // and look at the errors in more detail.
        return (String) reportFunc.call(Context.getCurrentContext(), scope,
                scope, args);
    }

    /**
     * Clear out all options that have been set with {@link #addOption(Option)}.
     */
    public void resetOptions() {
        options.clear();
    }

}
