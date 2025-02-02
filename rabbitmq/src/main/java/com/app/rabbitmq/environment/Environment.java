package com.app.rabbitmq.environment;

public class Environment {
    private String queueName = "video_processing_queue";
    private String exchangeName = "video_processing_exchange";
    private String routingKey = "video_processing_f78W*awdaW#$bDAW";

    public String getQueueName() {
        return queueName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
