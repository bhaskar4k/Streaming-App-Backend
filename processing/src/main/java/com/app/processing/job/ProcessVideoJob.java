package com.app.processing.job;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessVideoJob {
    private static Timer timer = new Timer();

    public static void start() {
        System.out.println("Service started...");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                log("Service is running at: " + LocalDateTime.now());
            }
        }, 0, 5000); // Runs every 5 seconds
    }

    public static void stop() {
        System.out.println("Service stopped...");
        timer.cancel();
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
