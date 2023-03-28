package dev.xfj;

import java.util.function.Consumer;

public class Timer {
    private final String name;
    private final Consumer<ProfileResult> func;
    private final long startTimePoint;
    private boolean stopped;

    public Timer(String name, Consumer<ProfileResult> func) {
        this.name = name;
        this.func = func;
        this.startTimePoint = System.nanoTime();
        this.stopped = false;
    }

    public void stop() {
        if (!stopped) {
            long endTimePoint = System.nanoTime();
            stopped = true;
            float duration = (endTimePoint - startTimePoint) / 1000000.0f;
            func.accept(new ProfileResult(name, duration));
        }
    }

    public static void profileScope(String name, Consumer<ProfileResult> consumer, Runnable runnable) {
        Timer timer = new Timer(name, consumer);
        try {
            runnable.run();
        } finally {
            timer.stop();
        }
    }
}
