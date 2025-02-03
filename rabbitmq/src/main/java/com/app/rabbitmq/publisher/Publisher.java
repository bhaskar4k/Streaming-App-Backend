package com.app.rabbitmq.publisher;

import com.app.rabbitmq.environment.Environment;
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

    public Publisher(){
        this.environment = new Environment();
    }

    @PostMapping("/")
    public String publishIntoQueue(@RequestBody String filePath){
        template.convertAndSend(environment.getExchangeName(),environment.getRoutingKey(),filePath);
        return "Places in queue";
    }
}
