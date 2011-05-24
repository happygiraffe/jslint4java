package com.googlecode.jslint4java;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;

/**
 * A collection of useful routines.
 *
 * @author dom
 */
final class Util {

    // Non-instantiable.
    private Util() {
    }

    /**
     * An object that can convert a JavaScript object into a Java one.
     */
    interface Converter<T> {
        /**
         * Turn the JavaScript object <i>obj</i> into a Java one. NB: For some
         * values (e.g. strings), this may be a regular Java object.
         */
        T convert(Object obj);
    }

    /**
     * Return the boolean value of a JavaScript variable. If the variable is
     * undefined, <i>false</i> is returned.
     *
     * @param name
     *            the JavaScript variable
     * @param scope
     *            the JavaScript scope to read from
     * @return the value of <i>name</i>
     */
    static boolean booleanValue(String name, Scriptable scope) {
        Object val = scope.get(name, scope);
        if (val == UniqueTag.NOT_FOUND) {
            return false;
        } else {
            return Context.toBoolean(val);
        }
    }

    /**
     * Return the integer value of a JavaScript variable.
     *
     * @param name
     *            the JavaScript variable
     * @param scope
     *            the JavaScript scope to read from
     * @return the value of <i>name</i> as an integer, or zero.
     */
    static int intValue(String name, Scriptable scope) {
        if (scope == null) {
            return 0;
        }
        Object o = scope.get(name, scope);
        return o == Scriptable.NOT_FOUND ? 0 : (int) Context.toNumber(o);
    }

    /**
     * Convert a Java object to a JavaScript one. This is basically like {@link Context#javaToJS}
     * except that we convert arrays to real JavaScript arrays instead of {@link NativeJavaArray}
     * instances. This is done in order to satisfy JSLint's implementation of
     * {@code Array.isArray()}.
     *
     * @param o
     *            Any Java object.
     * @param scope
     *            The scope within which the conversion is made.
     * @return An equivalent JavaScript object.
     */
    static Object javaToJS(Object o, Scriptable scope) {
        Class<?> cls = o.getClass();
        if (cls.isArray()) {
            return new NativeArray((Object[]) o);
        } else {
            return Context.javaToJS(o, scope);
        }
    }

    /**
     * Convert a JavaScript array into a Java {@link List}. You must provide a
     * converter which will be called on each value in order to convert it to a
     * Java object.
     *
     * @param <T>
     *            The type of every array member.
     * @param name
     *            The name of the array.
     * @param scope
     *            The scope which contains the array.
     * @param c
     *            A {@link Converter} instance to change the JavaScript object
     *            into a Java one.
     * @return A {@link List} of Java objects.
     */
    static <T> List<T> listValue(String name, Scriptable scope, Converter<T> c) {
        Object val = scope.get(name, scope);
        if (val == UniqueTag.NOT_FOUND || val instanceof Undefined) {
            return new ArrayList<T>();
        }

        Scriptable ary = (Scriptable) val;
        int count = intValue("length", ary);
        List<T> list = new ArrayList<T>(count);
        for (int i = 0; i < count; i++) {
            list.add(c.convert(ary.get(i, ary)));
        }
        return list;
    }

    /**
     * Convert a JavaScript array into a Java {@link List}, where each value in
     * that array is a Java object (or castable to it).
     *
     * @param <T>
     *            Normally, {@link String} or {@link Integer}.
     * @param name
     *            The name of the array to convert
     * @param class1
     *            The type of the array values
     * @param scope
     *            The scope containing the array.
     */
    static <T> List<T> listValueOfType(String name, Class<T> class1, Scriptable scope) {
        return listValue(name, scope, new Converter<T>() {
            public T convert(Object obj) {
                @SuppressWarnings("unchecked")
                T value = (T) obj;
                return value;
            }
        });
    }

    /**
     * Read all of a {@link Reader} into memory as a {@link String}.
     *
     * @param reader
     * @return
     * @throws IOException
     */
    static String readerToString(Reader reader) throws IOException {
        StringBuffer sb = new StringBuffer();
        int c;
        while ((c = reader.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    /**
     * Returns the value of a JavaScript variable, or null.
     *
     * @param name
     *            the JavaScript variable.
     * @param scope
     *            the JavaScript scope to read from
     * @return the value of <i>name</i> or null.
     */
    static String stringValue(String name, Scriptable scope) {
        if (scope == null) {
            return null;
        }
        Object o = scope.get(name, scope);
        return o instanceof String ? (String) o : null;
    }

}
