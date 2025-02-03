package com.app.rabbitmq.consumer;

import com.app.rabbitmq.common.CommonReturn;
import com.app.rabbitmq.environment.Environment;

import com.app.rabbitmq.model.Video;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/consumer")
public class Consumer {
    private Environment environment;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public Consumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.environment = new Environment();
    }

    @PostMapping("/pull")
    public CommonReturn<Boolean> consumeMessageFromQueue() throws JsonProcessingException {
        Video video = (Video) rabbitTemplate.receiveAndConvert("video_processing_queue");
//        ObjectMapper objectMapper = new ObjectMapper();
//        Video video = objectMapper.readValue(jsonString, Video.class);
        return pullFromQueueAndStartProcessingVideo(video);
    }

    public CommonReturn<Boolean> pullFromQueueAndStartProcessingVideo(Video video){
        String PROCESS_SERVICE_URL = environment.getProcessVideoServiceURL();

        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Video> entity = new HttpEntity<>(video, headers);

            ResponseEntity<CommonReturn<Boolean>> response = restTemplate.exchange(
                    PROCESS_SERVICE_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<CommonReturn<Boolean>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            //log(null,"validateToken()",e.getMessage());
            return CommonReturn.error(400,"Internal Server Error.");
        }
    }
}
