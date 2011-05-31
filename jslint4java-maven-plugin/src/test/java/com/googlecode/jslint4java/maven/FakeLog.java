package com.googlecode.jslint4java.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;

/**
 * An implementation of maven's {@link Log} interface, so we can check what
 * actually gets called.
 */
public class FakeLog implements Log {
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    };

    public static class LogItem {
        public final Level level;
        public final CharSequence msg;
        public final Throwable err;

        public LogItem(Level level, CharSequence msg, Throwable err) {
            this.level = level;
            this.msg = msg;
            this.err = err;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (%s)", level, msg, err);
        }
    }

    /** The public interface to what's been logged. */
    public List<LogItem> loggedItems = new ArrayList<FakeLog.LogItem>();

    public void debug(CharSequence content) {
        push(Level.DEBUG, content, null);
    }

    public void debug(CharSequence content, Throwable error) {
        push(Level.DEBUG, content, error);
    }

    public void debug(Throwable error) {
        push(Level.DEBUG, null, error);
    }

    public void error(CharSequence content) {
        push(Level.ERROR, content, null);
    }

    public void error(CharSequence content, Throwable error) {
        push(Level.ERROR, content, error);
    }

    public void error(Throwable error) {
        push(Level.ERROR, null, error);
    }

    public void info(CharSequence content) {
        push(Level.INFO, content, null);
    }

    public void info(CharSequence content, Throwable error) {
        push(Level.INFO, content, error);
    }

    public void info(Throwable error) {
        push(Level.INFO, null, error);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public boolean isWarnEnabled() {
        return true;
    }

    private void push(Level level, CharSequence content, Throwable error) {
        loggedItems.add(new LogItem(level, content, error));
    }

    public void warn(CharSequence content) {
        push(Level.WARN, content, null);
    }

    public void warn(CharSequence content, Throwable error) {
        push(Level.WARN, content, error);
    }

    public void warn(Throwable error) {
        push(Level.WARN, null, error);
    }
}