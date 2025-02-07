package com.app.rabbitmq.consumer;

import com.app.rabbitmq.common.CommonReturn;
import com.app.rabbitmq.entity.TLogExceptions;
import com.app.rabbitmq.environment.Environment;

import com.app.rabbitmq.model.Video;
import com.app.rabbitmq.service.LogExceptionsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer")
public class Consumer {
    private Environment environment;
    private final RabbitTemplate rabbitTemplate;
    @Autowired
    private LogExceptionsService logExceptionsService;

    @Autowired
    public Consumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.environment = new Environment();
    }

    @GetMapping("/pull")
    public CommonReturn<Video> consumeMessageFromQueue(){
        try{
            Video video = (Video) rabbitTemplate.receiveAndConvert(environment.getQueueName());
            if(video == null){
                return CommonReturn.error(404,"Queue is empty.");
            }

            return CommonReturn.success("Video Metadata pulled from queue.",video);
        } catch (Exception e) {
            log(0L,"consumeMessageFromQueue()",e.getMessage());
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
