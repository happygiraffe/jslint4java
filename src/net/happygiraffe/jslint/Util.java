package net.happygiraffe.jslint;

import org.mozilla.javascript.Scriptable;

class Util {

    static int intValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof Number ? ((Number) o).intValue() : 0; 
    }

    static String stringValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof String ? (String) o : null;
    }
}
