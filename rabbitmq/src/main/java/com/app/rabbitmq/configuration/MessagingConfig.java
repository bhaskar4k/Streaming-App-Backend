package com.app.rabbitmq.configuration;

import com.app.rabbitmq.environment.Environment;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {
    Environment environment;

    public MessagingConfig(){
        this.environment = new Environment();
    }

    @Bean
    public Queue queue(){
        return new Queue(environment.getQueueName());
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(environment.getExchangeName());
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(environment.getRoutingKey()).noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
