package ru.emil.tgvectorization.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    public static final String TG_EXCHANGE = "tg-virtualization";
    public static final String INBOUND_RK = "tg.virtualization.key.qu.vector.inbound";

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${ru.emil.rabbit.url}") String url,
            @Value("${ru.emil.rabbit.port}") int port,
            @Value("${ru.emil.rabbit.username}") String username,
            @Value("${ru.emil.rabbit.password}") String password
    ) {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(url, port);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }
}
