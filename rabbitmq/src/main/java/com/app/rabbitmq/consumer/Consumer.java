package com.app.rabbitmq.consumer;

import com.app.rabbitmq.environment.Environment;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumer")
public class Consumer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public Consumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/")
    public String consumeMessageFromQueue() {
        String filePath = (String) rabbitTemplate.receiveAndConvert("video_processing_queue");

        if (filePath != null) {
            System.out.println("Filepath received from queue: " + filePath);
            return "File processed: " + filePath;
        } else {
            return "No messages in the queue.";
        }
    }
}
