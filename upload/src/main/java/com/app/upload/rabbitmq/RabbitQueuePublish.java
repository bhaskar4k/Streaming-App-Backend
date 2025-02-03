package com.app.upload.rabbitmq;

import com.app.upload.service.LogExceptionsService;
import com.app.upload.common.CommonReturn;
import com.app.upload.entity.TLogExceptions;
import com.app.upload.environment.Environment;
import com.app.upload.model.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class RabbitQueuePublish {
    @Autowired
    private LogExceptionsService logExceptionsService;
    private Environment environment;

    public RabbitQueuePublish(){
        this.environment = new Environment();
    }

    public CommonReturn<Boolean> publishIntoRabbitMQ(Video video){
        String VIDEO_PUBLISH_TO_RABBITMQ_URL = environment.getRabbitMQPublishURL();

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Video> entity = new HttpEntity<>(video, headers);

            ResponseEntity<CommonReturn<Boolean>> response = restTemplate.exchange(
                    VIDEO_PUBLISH_TO_RABBITMQ_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<CommonReturn<Boolean>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log(null,"validateToken()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
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
