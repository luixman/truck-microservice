package ru.truckfollower.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Getter
public class RabbitMqReceiveConfig {
    /**
     *Компоненты для получения сообщений
     */

    @Value("${rabbitmq.receive.queue.name}")
    private String queue;
    @Value("${rabbitmq.receive.exchange}")
    private String exchange;
    @Value("${rabbitmq.receive.routing-key}")
    private String routingKey;
    @Value("${rabbitmq.receive.queue.durable}")
    private boolean durable;

    @Value("${rabbitmq.receive.queue.exclusive}")
    private boolean exclusive;

    @Bean
    @Primary
    public Queue receiveQueue(){
        //  return new Queue(queue,durable,exclusive,true);
        return new Queue(queue);
    }

    @Bean
    @Primary
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    @Primary
    public Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }

}
