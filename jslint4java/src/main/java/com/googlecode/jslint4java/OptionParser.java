package com.googlecode.jslint4java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A utility class for turning option arguments from strings to typed values.
 *
 * @author dom
 *
 */
class OptionParser {

    /**
     * Attempt to parse <i>value</i> using the {@code valueOf(String)} method on <i>clazz</i>,
     * should one exist.
     */
    public <T> T parse(Class<T> clazz, String value) {
        try {
            Method method = clazz.getMethod("valueOf", String.class);
            // There's no contract for this, but in the cases we need it for, it
            // should work.
            @SuppressWarnings("unchecked")
            T rv = (T) method.invoke(null, value);
            return rv;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // Can never happen. If the method can't be accessed, we get a NoSuchMethodException
            // first, instead.
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                // Attempt to rethrow original exception.
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }
}
