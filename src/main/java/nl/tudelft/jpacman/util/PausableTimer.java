package nl.tudelft.jpacman.util;

import javax.swing.Timer;
import java.awt.event.ActionListener;

/** A timer that can be paused and resumed. It does not repeat. */
public class PausableTimer {
    private final Timer timer;
    private final int delay;
    private long startTimeMillis;
    private boolean isPaused;

    /** Creates an instance of PausableTimer.
     * @param delay the callback delay
     * @param listener the callback
     */
    public PausableTimer(int delay, ActionListener listener) {
        this.timer = new Timer(delay, listener);
        this.timer.setRepeats(false);
        this.delay = delay;
        this.isPaused = false;
    }

    /** Starts the timer. */
    public void start() {
        startTimeMillis = System.currentTimeMillis();
        this.timer.setInitialDelay(this.delay);
        this.timer.start();
    }

    /** Stops the timer. */
    public void stop() {
        this.timer.stop();
    }

    /** Pauses the timer. */
    public void pause() {
        if (!this.timer.isRunning()) {
            return;
        }

        // convertion should not be a problem for our application
        var timeLeft = this.timer.getDelay() - (int)(System.currentTimeMillis() - startTimeMillis);
        this.timer.stop();
        this.timer.setInitialDelay(timeLeft);
        this.isPaused = true;
    }

    /** Resumes the timer */
    public void resume() {
        if (!this.isPaused) {
            return;
        }
        startTimeMillis = System.currentTimeMillis();
        this.timer.start();
        this.isPaused = false;
    }

    /** Whether the timer is running */
    public boolean isRunning() {
        return this.timer.isRunning();
    }
}
