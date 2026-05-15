package at.fhtw.energy.usageservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ENERGY_QUEUE = "energy.queue";
    public static final String UPDATE_QUEUE = "energy.update.queue";

    @Bean
    public Queue energyQueue() {
        return new Queue(ENERGY_QUEUE, true);
    }

    @Bean
    public Queue updateQueue() {
        return new Queue(UPDATE_QUEUE, true);
    }
}