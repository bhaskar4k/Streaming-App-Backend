package com.app.processing.python;

import com.app.processing.entity.TLogExceptions;
import com.app.processing.service.LogExceptionsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PythonInvoker {
    @Autowired
    private LogExceptionsService logExceptionsService;

    public boolean runPythonScript(long t_mst_user_id, String scriptPath, String... args) {
        StringBuilder command = new StringBuilder("python " + scriptPath);
        for (String arg : args) {
            command.append(" ").append(arg);
        }

        try {
            Process process = Runtime.getRuntime().exec(command.toString());

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
                    log(t_mst_user_id,"runPythonScript()",e.getMessage());
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
                    log(t_mst_user_id,"runPythonScript()",e.getMessage());
                }
            });

            outputThread.start();
            errorThread.start();

            outputThread.join();
            errorThread.join();

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                System.err.println("Python script exited with errors.");
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log(t_mst_user_id,"runPythonScript()",e.getMessage());
        }

        return false;
    }


    private void log(Long t_mst_user_id, String function_name, String exception_msg){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        String full_class_path = stackTraceElements[2].getClassName();
        String class_name = full_class_path.substring(full_class_path.lastIndexOf(".") + 1);

        String full_package_path = full_class_path.substring(0, full_class_path.lastIndexOf("."));
        String package_name = full_package_path.substring(full_package_path.lastIndexOf(".") + 1);

        logExceptionsService.saveLogException(new TLogExceptions(package_name,class_name,function_name,exception_msg,t_mst_user_id));
    }
}
