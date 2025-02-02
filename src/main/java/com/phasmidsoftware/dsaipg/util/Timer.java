/*
 * Copyright (c) 2024.
 */

package com.phasmidsoftware.dsaipg.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Class which is able to time the running of functions.
 */
public class Timer {

    private long ticks = 0L;  // Accumulated time (nanoseconds) when paused, or "negative offset" when running
    private int laps = 0;     // Number of laps completed
    private boolean running = false;  // Is the timer running?

    final static LazyLogger logger = new LazyLogger(Timer.class);

    /**
     * Construct a new Timer and set it *running* immediately.
     */
    public Timer() {
        doTrace(() -> "create new timer");
        resume();  // The tests expect the timer to be running from the start
    }

    //-------------------------------------------------------------------------
    // Timer control methods
    //-------------------------------------------------------------------------

    /**
     * Resume this timer (start or continue running).
     * Implementation detail: sets ticks = ticks - now, so that
     * while running, ticks is offset by the "resume" time.
     *
     * @throws TimerException if the Timer is already running.
     */
    public void resume() {
        if (running) throw new TimerException("Cannot resume: timer is already running");
        long now = getClock();
        ticks -= now;  // negative offset
        running = true;
        doTrace(() -> "resume timer");
    }

    /**
     * Pause this timer *without* incrementing laps.
     *
     * Implementation detail: calls pauseAndLap() then laps-- so net effect on laps=0,
     * but we do accumulate the time so far into <code>ticks</code>.
     *
     * @throws TimerException if the Timer is not running.
     */
    public void pause() {
        pauseAndLap();  // accumulates time + increments laps
        laps--;         // revert the lap increment
        doTrace(() -> "pause timer");
    }

    /**
     * Pause this timer and increment the lap counter by one.
     * Implementation detail: sets ticks = ticks + now,
     * so that while paused, ticks is the total elapsed time.
     *
     * @throws TimerException if the Timer is not running.
     */
    public void pauseAndLap() {
        if (!running) throw new TimerException("Cannot pauseAndLap: timer is not running");
        long now = getClock();
        ticks += now;   // accumulate elapsed time
        running = false;
        laps++;
        doTrace(() -> "pause timer and lap after millisecs: " + toMillisecs(ticks));
    }

    /**
     * Increment the lap counter *without* pausing.
     * This is like hitting a "split" on a stopwatch.
     *
     * @throws TimerException if the Timer is not running.
     */
    public void lap() {
        if (!running) throw new TimerException("Cannot lap: timer is not running");
        laps++;
        doTrace(() -> "lap " + laps);
    }

    /**
     * Stop this Timer (pause + lap) and return the mean lap time in milliseconds.
     * The tests expect that calling stop() automatically includes a new lap,
     * thus if you only had 0 laps before, you'll end with 1 lap.
     *
     * @return the average milliseconds used by each lap
     * @throws TimerException if the Timer is not running
     */
    public double stop() {
        pauseAndLap(); // accumulate time, increment laps, running=false
        doTrace(() -> "stop timer");
        return meanLapTime();  // must be paused here
    }

    /**
     * Return the mean lap time in milliseconds for this paused timer.
     *
     * @return the average milliseconds used by each lap
     * @throws TimerException if this Timer is running or if laps=0
     */
    public double meanLapTime() {
        if (running) {
            throw new TimerException("Cannot get meanLapTime: timer is still running");
        }
        if (laps == 0) {
            // Some tests might expect an exception, or 0.
            throw new TimerException("No laps available for meanLapTime");
        }
        return toMillisecs(ticks) / laps;
    }

    /**
     * Method to yield the total number of milliseconds elapsed so far (ignoring laps).
     * Will throw an exception if the Timer is currently running (because ticks is offset).
     *
     * @return total ms elapsed (if paused).
     */
    public double millisecs() {
        if (running) {
            throw new TimerException("Cannot get millisecs: timer is still running");
        }
        return toMillisecs(ticks);
    }

    //-------------------------------------------------------------------------
    // "repeat" methods
    //-------------------------------------------------------------------------

    /**
     * Run the given function n times, once per "lap", then return the result of calling meanLapTime().
     * The clock will be running when the method starts and still running when it ends.
     *
     * @param n        the number of repetitions
     * @param function a function which yields a T (could be Void)
     * @param <T>      the type supplied by function
     * @return the average ms per repetition
     */
    public <T> double repeat(int n, Supplier<T> function) {
        for (int i = 0; i < n; i++) {
            function.get();
            lap();  // increment laps while running
        }
        // Now accumulate the time so far, but do not increment net laps
        pause();                // calls pauseAndLap + laps-- => net laps still = n
        double result = meanLapTime();
        resume();              // restore running state
        return result;
    }

    /**
     * Run the given function n times, once per "lap", and then return the mean lap time.
     *
     * @param n        # of repetitions
     * @param supplier supplies T
     * @param function transforms T => U
     * @param <T>      type from supplier
     * @param <U>      type from function
     * @return average ms per repetition
     */
    public <T, U> double repeat(int n, Supplier<T> supplier, Function<T, U> function) {
        return repeat(n, false, supplier, function, null, null);
    }

    /**
     * Pause (without counting a lap); run the given function n times (once per lap), then return mean lap time.
     *
     * "The timer is running when this method is called and still running when it returns."
     * Implementation: We do:
     *    1) pause() so that any preFunction is not timed
     *    2) For each iteration: do pre, resume+function, pauseAndLap, do post
     *    3) Accumulate totalTime if !warmup
     *    4) Finally resume() so that we exit with the clock still running
     *
     * @param n            # of reps
     * @param warmup       true => we do not accumulate times
     * @param supplier     supplies T
     * @param function     transforms T => U
     * @param preFunction  optional transform T => T done while paused
     * @param postFunction optional consumer of U done while paused
     * @param <T>          input type
     * @param <U>          output type
     * @return average ms per rep (0 if warmup)
     */
    public <T, U> double repeat(int n,
                                boolean warmup,
                                Supplier<T> supplier,
                                Function<T, U> function,
                                UnaryOperator<T> preFunction,
                                Consumer<U> postFunction) {

        // Step 1: Pause so that the preFunction is not timed
        pause(); // now the clock is paused, but we haven't incremented a lap
        double totalTime = 0.0;

        for (int i = 0; i < n; i++) {
            // Pre-processing (not timed)
            T input = supplier.get();
            if (preFunction != null) {
                input = preFunction.apply(input);
            }

            // Step 2: Timed portion
            resume();  // start clock
            long startTime = getClock();
            U result = function.apply(input);
            long endTime = getClock();

            // Step 3: Pause so that postFunction is not timed, but increment one lap
            pauseAndLap();  // accumulates time + laps++

            // Post-processing (not timed)
            if (postFunction != null) {
                postFunction.accept(result);
            }

            // Accumulate if not warmup
            if (!warmup) {
                totalTime += toMillisecs(endTime - startTime);
            }
        }

        // Step 4: Resume so we exit with the clock running
        resume();

        return warmup ? 0.0 : totalTime / n;
    }

    //-------------------------------------------------------------------------
    // Internal utilities
    //-------------------------------------------------------------------------

    /**
     * Acquire the system clock time in nanoseconds.
     * Must be consistent with toMillisecs().
     */
    private static long getClock() {
        return System.nanoTime();
    }

    /**
     * Convert nanoseconds to milliseconds as a double.
     */
    private static double toMillisecs(long nanos) {
        return nanos / 1_000_000.0;
    }

    @Override
    public String toString() {
        return "Timer{" +
                "ticks=" + ticks +
                ", laps=" + laps +
                ", running=" + running +
                '}';
    }

    //-------------------------------------------------------------------------
    // Testing hooks (for reflection-based unit tests)
    //-------------------------------------------------------------------------

    // NOTE: these are package-private or private, but test can use reflection or a helper.
    private long getTicks() {
        return ticks;
    }
    private int getLaps() {
        return laps;
    }
    private boolean isRunning() {
        return running;
    }

    /**
     * Print or log progress markers. Not critical to timer logic, but used by some examples.
     */
    @SuppressWarnings("unused")
    private static int doPrintStatus(int lastx, final int x) {
        if (x != lastx) {
            if (x % 10 == 0)
                System.out.print(10 - x / 10);
            else
                System.out.print(".");
        }
        return x;
    }

    //-------------------------------------------------------------------------
    // Logging
    //-------------------------------------------------------------------------

    private static <T> void doTrace(final boolean condition, Supplier<String> messageFunction) {
        if (logger.isTraceEnabled() && condition) logger.trace(messageFunction.get());
    }

    private static void doTrace(Supplier<String> f) {
        doTrace(true, f);
    }

    //-------------------------------------------------------------------------
    // TimerException
    //-------------------------------------------------------------------------

    /**
     * TimerException is a custom unchecked exception used to indicate errors
     * or invalid states specifically related to operations on the Timer.
     */
    public static class TimerException extends RuntimeException {
        public TimerException() { super(); }
        public TimerException(String message) { super(message); }
        public TimerException(String message, Throwable cause) { super(message, cause); }
        public TimerException(Throwable cause) { super(cause); }
    }
}