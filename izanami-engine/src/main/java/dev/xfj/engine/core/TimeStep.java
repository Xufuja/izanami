package dev.xfj.engine.core;

public class TimeStep {
    private final float time;

    public TimeStep() {
        this(0.0f);
    }

    public TimeStep(float time) {
        this.time = time;
    }

    public float getSeconds() {
        return time;
    }

    public float getMilliseconds() {
        return time * 1000.0f;
    }

    public float getTime() {
        return time;
    }
}
