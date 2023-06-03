package dev.xfj.engine.core;

public class Timer {
    private long startTimePoint;

    public Timer() {
        reset();
    }

    public void reset() {
        this.startTimePoint = System.nanoTime();
    }

    public float elapsed() {
        return (System.nanoTime() - startTimePoint)  * 0.001f * 0.001f * 0.001f;
    }
    public float elapsedMillis() {
        return elapsed() * 1000.0f;
    }
}
