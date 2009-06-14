package net.happygiraffe.jslint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A utility class for turning option arguments from strings to typed values.
 *
 * @author dom
 *
 */
public class OptionParser {

    /**
     * Attempt to parse <i>value</i> using the {@code valueOf(String)} method on
     * <i>clazz</i>, should one exist.
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
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
