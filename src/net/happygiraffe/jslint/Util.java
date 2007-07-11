package net.happygiraffe.jslint;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

class Util {

    static int intValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof Undefined ? 0 : ((Double) o).intValue(); 
    }

    static String stringValue(String name, Scriptable scope) {
        Object o = scope.get(name, scope);
        return o instanceof Undefined ? null : (String) o;
    }
}
