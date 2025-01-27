package com.app.upload.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class PythonInvoker {
    public void runPythonScript(String scriptPath, String... args) {
        StringBuilder command = new StringBuilder("python " + scriptPath);
        for (String arg : args) {
            command.append(" ").append(arg);
        }

        try {
            // Start the process
            Process process = Runtime.getRuntime().exec(command.toString());

            // Create threads to handle stdout and stderr
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            Thread outputThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread errorThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        System.err.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            outputThread.start();
            errorThread.start();

            // Wait for the process to complete with a timeout
//            if (!process.waitFor(300, TimeUnit.SECONDS)) { // Timeout of 300 seconds
//                process.destroy(); // Force terminate if timeout
//                throw new RuntimeException("Python script timed out");
//            }

            outputThread.join();
            errorThread.join();

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                System.err.println("Python script exited with errors.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error running Python script: " + e.getMessage(), e);
        }
    }
}
