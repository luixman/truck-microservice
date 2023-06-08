package ru.truckfollower.config;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMqSendConfig {
    /**
     *компоненты для отправки сообщений
     */
    @Value("${rabbitmq.send.queue.name}")
    private String Queue;

    @Value("${rabbitmq.send.exchange}")
    private String exchange;

    @Value("${rabbitmq.send.routing-key}")
    private String routingKey;

    @Value("${rabbitmq.send.queue.durable}")
    private boolean durable;

    @Value("${rabbitmq.send.queue.exclusive}")
    private boolean exclusive;

    @Bean
    public Queue sendAlarmQueue(){
        return new Queue(Queue);
    }

    @Bean
    public TopicExchange sendExchange(){
        return new TopicExchange(exchange);
    }
    @Bean
    public Binding sendBinding(@Qualifier("sendAlarmQueue") Queue queue, @Qualifier("sendExchange") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}
