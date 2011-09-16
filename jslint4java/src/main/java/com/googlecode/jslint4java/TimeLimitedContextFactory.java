package com.googlecode.jslint4java;

import java.util.concurrent.TimeUnit;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

/**
 * A ContextFactory that allows specification of a timeout. This is mostly
 * copied from the example in the ContextFactory javadocs.
 */
public class TimeLimitedContextFactory extends ContextFactory {

    /** Check time after this many ops. */
    private static final int OPS_QUANTUM = 10000;

    @SuppressWarnings("serial")
    public static class TimeExceededException extends IllegalStateException {
        public TimeExceededException() {
            super();
        }
    }

    private static class TimeLimitedContext extends Context {
        public TimeLimitedContext(TimeLimitedContextFactory timeLimitedContextFactory) {
            super(timeLimitedContextFactory);
        }

        private long startTime;
    }

    private final long maxTimeNanos;

    /**
     * Create a new {@link TimeLimitedContextFactory}.
     *
     * @param maxTime
     *            the maximum amount of time that a JavaScript execution is
     *            allowed to take.
     * @param timeUnit
     *            the unit of maxTime.
     */
    public TimeLimitedContextFactory(long maxTime, TimeUnit timeUnit) {
        maxTimeNanos = timeUnit.toNanos(maxTime);
    }

    /** Create a TimeLimitedContext that runs the observer every 10k ops. */
    @Override
    protected Context makeContext() {
        TimeLimitedContext cx = new TimeLimitedContext(this);
        cx.setInstructionObserverThreshold(OPS_QUANTUM);
        return cx;
    }

    /** Record start time in context. */
    @Override
    protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj,
            Object[] args) {
        TimeLimitedContext tlcx = (TimeLimitedContext) cx;
        tlcx.startTime = System.nanoTime();
        return super.doTopCall(callable, tlcx, scope, thisObj, args);
    }

    /** Enforce time restrictions. */
    @Override
    protected void observeInstructionCount(Context cx, int instructionCount) {
        TimeLimitedContext tlcx = (TimeLimitedContext) cx;
        long currentTime = System.nanoTime();
        long durationNanos = currentTime - tlcx.startTime;
        if (durationNanos > maxTimeNanos) {
            throw new TimeExceededException();
        }
    }
}
