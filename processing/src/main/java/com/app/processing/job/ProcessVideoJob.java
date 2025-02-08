package com.app.processing.job;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessVideoJob {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static int running_thread = 0;

    public static void start() {
        System.out.println("Service started...");
        scheduleNextRun(0);
    }

    private static void scheduleNextRun(long delay) {
        scheduler.schedule(() -> {
            if (running_thread < 4) {
                log("Service - " + running_thread + " is running at - " + LocalDateTime.now());
                running_thread++;
                scheduleNextRun(1); // Continue every second
            } else {
                running_thread = 0;
                log("Pausing for 6 seconds...");
                scheduleNextRun(6); // Pause for 6 seconds
            }
        }, delay, TimeUnit.SECONDS);
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
