package at.fhtw.energy.energyproducer.service;

import at.fhtw.energy.energyproducer.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class EnergyProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final WeatherService weatherService;
    private final Random random = new Random();

    public EnergyProducerService(RabbitTemplate rabbitTemplate,
                                 WeatherService weatherService) {
        this.rabbitTemplate = rabbitTemplate;
        this.weatherService = weatherService;
    }

    @Scheduled(fixedDelay = 3000) // alle 3 Sekunden
    public void sendProducerMessage() {
        double cloudCover = weatherService.getCloudCover();

        // je weniger Wolken, desto mehr Energie
        double maxKwh = 0.008 * (1 - cloudCover / 100.0);
        double minKwh = 0.001;
        double kwh = minKwh + (maxKwh - minKwh) * random.nextDouble();
        kwh = Math.round(kwh * 10000.0) / 10000.0;

        String datetime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String message = String.format(
                java.util.Locale.US,  // ← das hinzufügen!
                "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\"," +
                        "\"kwh\":%.4f,\"datetime\":\"%s\"}",
                kwh, datetime);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        System.out.println("Sent: " + message);
    }
}