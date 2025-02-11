package com.app.processing.job;
import com.app.processing.common.CommonReturn;
import com.app.processing.common.Util;
import com.app.processing.environment.Environment;
import com.app.processing.model.Video;
import com.app.processing.python.PythonInvoker;
import com.app.processing.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProcessVideoJob {
    @Autowired
    private static ProcessingService processingService;

    private static Environment environment;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static int running_thread = 0;

    public ProcessVideoJob(){
        this.environment = new Environment();
    }

    public static void start() {
        System.out.println("Service started...");
        scheduleNextRun(0);
    }

    private static void scheduleNextRun(long delay) {
        scheduler.schedule(() -> {
            if (running_thread < 4) {
                log("Processing thread no :- " + running_thread + " is running at - " + LocalDateTime.now());
                running_thread++;

                CommonReturn<Boolean> response = pullFromQueueAndStartProcessingVideo();
                if(response.getStatus()==200){
                    //Send message to user that his video has been processed
                }

                scheduleNextRun(25); // Continue every 25 second
            } else {
                running_thread = 0;
                log("Pausing for 6 seconds...");
                scheduleNextRun(6); // Pause for 6 seconds
            }
        }, delay, TimeUnit.SECONDS);
    }

    public static CommonReturn<Boolean> pullFromQueueAndStartProcessingVideo(){
        String RABBITMQ_CONSUMER_URL = environment.getRabbitMQConsumerURL();

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<CommonReturn<Video>> response = restTemplate.exchange(
                    RABBITMQ_CONSUMER_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<CommonReturn<Video>>() {}
            );

            if(response.getBody().getStatus()!=200){
                return CommonReturn.error(response.getBody().getStatus(),response.getBody().getMessage());
            }

            Boolean status = processingService.encodeVideo(response.getBody().getData());
            running_thread--;

            return CommonReturn.success("Video Processing done.", status);
        } catch (Exception e) {
            //log(null,"validateToken()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }
}
