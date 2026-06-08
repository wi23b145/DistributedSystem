package at.fhtw.energy.energyuser.service;

import at.fhtw.energy.energyuser.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class EnergyUserService {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public EnergyUserService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000) // alle 5 Sekunden
    public void sendUserMessage() {
        double kwh = calculateKwh();

        String datetime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String message = String.format(
                java.util.Locale.US,
                "{\"type\":\"USER\",\"association\":\"COMMUNITY\"," +
                        "\"kwh\":%.4f,\"datetime\":\"%s\"}",
                kwh, datetime);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        System.out.println("Sent: " + message);
    }

    private double calculateKwh() {
        int hour = LocalTime.now().getHour();
        double baseKwh;

        if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 20)) {
            baseKwh = 0.006;
        } else if (hour >= 22 || hour <= 5) {
            baseKwh = 0.001;
        } else {
            baseKwh = 0.003;
        }

        double variation = 1.0 + (random.nextDouble() * 0.4 - 0.2);
        double kwh = baseKwh * variation;
        return Math.round(kwh * 10000.0) / 10000.0;
    }
}