package at.fhtw.energy.energyuser.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "energy.queue";

    @Bean
    public Queue energyQueue() {
        return new Queue(QUEUE_NAME, true);
    }
}