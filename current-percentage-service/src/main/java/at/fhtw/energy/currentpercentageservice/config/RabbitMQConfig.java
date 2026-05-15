package at.fhtw.energy.currentpercentageservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String UPDATE_QUEUE = "energy.update.queue";

    @Bean
    public Queue updateQueue() {
        return new Queue(UPDATE_QUEUE, true);
    }
}