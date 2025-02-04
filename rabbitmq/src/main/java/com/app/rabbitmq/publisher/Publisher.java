package com.app.rabbitmq.publisher;

import com.app.rabbitmq.common.CommonReturn;
import com.app.rabbitmq.environment.Environment;
import com.app.rabbitmq.model.Video;
import com.app.rabbitmq.entity.TLogExceptions;
import com.app.rabbitmq.service.LogExceptionsService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publish")
public class Publisher {
    @Autowired
    private RabbitTemplate template;
    Environment environment;
    @Autowired
    private LogExceptionsService logExceptionsService;

    public Publisher(){
        this.environment = new Environment();
    }

    @PostMapping("/put_in_queue")
    public CommonReturn<Boolean> publishIntoQueue(@RequestBody Video video){
        try{
            template.convertAndSend(environment.getExchangeName(),environment.getRoutingKey(),video);
            return CommonReturn.success("placed in queue", true);
        } catch (Exception e) {
            log(video.getTMstUserId(),"publishIntoQueue()",e.getMessage());
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
