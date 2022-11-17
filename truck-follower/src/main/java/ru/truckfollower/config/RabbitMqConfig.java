package ru.truckfollower.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public final String queue;
    public final String exchange;
    public final String routingKey;
    public final boolean durable;
    public final boolean exclusive;


    public RabbitMqConfig(@Value("${rabbitmq.queue.name}") String queue, @Value("${rabbitmq.exchange}") String exchange, @Value("${rabbitmq.routingKey}") String routingKey,
                          @Value("${rabbitmq.queue.durable}") boolean durable, @Value("${rabbitmq.queue.exclusive}") boolean exclusive) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.durable = durable;
        this.exclusive = exclusive;
    }

    @Bean
    public Queue queue(){
        //  return new Queue(queue,durable,exclusive,true);
        return new Queue(queue);
    }

    @Bean
    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

}
