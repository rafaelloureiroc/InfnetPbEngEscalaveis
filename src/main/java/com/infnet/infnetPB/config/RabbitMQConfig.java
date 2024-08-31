package com.infnet.infnetPB.config;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;


@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter());
        return factory;
    }

    @Bean
    public Exchange mesaExchange() {
        return ExchangeBuilder.directExchange("mesaExchange").durable(true).build();
    }

    @Bean
    public Queue mesaReservadaQueue() {
        return new Queue("mesaReservadaQueue", true);
    }

    @Bean
    public Queue mesaCadastradaQueue() {
        return new Queue("mesaCadastradaQueue", true);
    }

    @Bean
    public Binding mesaReservadaBinding(Queue mesaReservadaQueue, Exchange mesaExchange) {
        return BindingBuilder.bind(mesaReservadaQueue).to(mesaExchange).with("mesaReservada").noargs();
    }

    @Bean
    public Binding mesaCadastradaBinding(Queue mesaCadastradaQueue, Exchange mesaExchange) {
        return BindingBuilder.bind(mesaCadastradaQueue).to(mesaExchange).with("mesaCadastrada").noargs();
    }


    @Bean
    public Exchange pedidoExchange() {
        return ExchangeBuilder.directExchange("pedidoExchange").durable(true).build();
    }

    @Bean
    public Queue pedidoCriadoQueue() {
        return new Queue("pedidoCriadoQueue", true);
    }

    @Bean
    public Binding pedidoCriadoBinding(Queue pedidoCriadoQueue, Exchange pedidoExchange) {
        return BindingBuilder.bind(pedidoCriadoQueue).to(pedidoExchange).with("pedidoCriado").noargs();
    }


    @Bean
    public Exchange restauranteExchange() {
        return ExchangeBuilder.directExchange("restauranteExchange").durable(true).build();
    }

    @Bean
    public Queue restauranteCadastradoQueue() {
        return new Queue("restauranteCadastradoQueue", true);
    }

    @Bean
    public Binding restauranteCadastradoBinding(Queue restauranteCadastradoQueue, Exchange restauranteExchange) {
        return BindingBuilder.bind(restauranteCadastradoQueue).to(restauranteExchange).with("restauranteCadastrado").noargs();
    }

}