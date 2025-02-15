package com.app.processing.job;
import com.app.processing.common.CommonReturn;
import com.app.processing.entity.TLogExceptions;
import com.app.processing.environment.Environment;
import com.app.processing.model.Video;
import com.app.processing.service.LogExceptionsService;
import com.app.processing.service.ProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Service
public class ProcessVideoJob {
    @Autowired
    private ProcessingService processingService;
    @Autowired
    private LogExceptionsService logExceptionsService;
    private Environment environment;

    private final int MAX_CONCURRENT_JOBS = 2;
    private final int POLL_INTERVAL_SECONDS = 20;

    private ScheduledExecutorService scheduler;
    private ExecutorService workerPool;
    private Semaphore semaphore;

    public ProcessVideoJob(){
        this.environment = new Environment();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.workerPool = Executors.newFixedThreadPool(MAX_CONCURRENT_JOBS);
        this.semaphore = new Semaphore(MAX_CONCURRENT_JOBS);

        this.start();
    }

    private void start(){
        System.out.println("Processing Job Is Started...");
        scheduler.scheduleAtFixedRate(this::pollRabbitMQServer, 0, POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void startPolling() {
        try {
            System.out.println("Job started...");
            scheduler.scheduleAtFixedRate(this::pollRabbitMQServer, 0, POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log(0L,"startPolling()",e.getMessage());
        }
    }

    private void pollRabbitMQServer() {
        if (semaphore.availablePermits() == 0) {
            System.out.println("All worker slots are busy. Skipping polling...");
            return;
        }

        System.out.println("Polling server...");
        CommonReturn<Video> job = pullFromRabbitMQ();

        if (job.getStatus()==200) {
            try {
                semaphore.acquire(); // Acquire a permit
                workerPool.execute(() -> executeJob(job.getData()));
            } catch (InterruptedException e) {
                log(0L,"pollServer()",e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private CommonReturn<Video> pullFromRabbitMQ(){
        String RABBITMQ_CONSUMER_URL = environment.getRabbitMQConsumerURL();

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<CommonReturn<Video>> response = restTemplate.exchange(
                    RABBITMQ_CONSUMER_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<CommonReturn<Video>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log(0L,"pullFromRabbitMQ()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }

    private void executeJob(Video video) {
        try {
            System.out.println(Thread.currentThread().getName() + " is executing video ID - " + video.getVIDEO_GUID());
            Future<Boolean> future = processingService.encodeVideo(video,workerPool);
            Boolean processing_done = future.get();

            if(processing_done){
                System.out.println(Thread.currentThread().getName() + " has executed video ID - " + video.getVIDEO_GUID());
            }else{
                System.out.println(Thread.currentThread().getName() + " has failed to execute video ID - " + video.getVIDEO_GUID());
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            log(0L,"executeJob()",e.getMessage());
        } finally {
            semaphore.release(); // Release the permit
        }
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
