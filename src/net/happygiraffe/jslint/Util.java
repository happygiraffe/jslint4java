package net.happygiraffe.jslint;

import org.mozilla.javascript.Scriptable;

/**
 * A collection of useful routines.
 * 
 * @author dom
 * @version $Id$
 */
class Util {

    /**
     * Return the integer value of a JavaScript variable.
     * 
     * @param name
     *                the JavaScript variable
     * @param scope
     *                the JavaScript scope to read from
     * @return the value of <i>name</i> as an integer, or zero.
     */
    static int intValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof Number ? ((Number) o).intValue() : 0;
    }

    /**
     * Returns the value of a JavaScript variable, or null.
     * 
     * @param name
     *                the JavaScript variable.
     * @param scope
     *                the JavaScript scope to read from
     * @return the value of <i>name</i> or null.
     */
    static String stringValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof String ? (String) o : null;
    }

    /**
     * Returns the value of a JavaScript variable, or false.
     * 
     * @param name
     *                the JavaScript variable.
     * @param scope
     *                the JavaScript scope to read from.
     * @return the value of <i>name</i> as a boolean, or false.
     */
    static boolean booleanValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof Boolean ? ((Boolean) o).booleanValue() : false;
    }
}
