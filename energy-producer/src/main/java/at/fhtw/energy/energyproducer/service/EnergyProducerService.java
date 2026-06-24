package at.fhtw.energy.energyproducer.service;

import at.fhtw.energy.energyproducer.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

    @EventListener(ApplicationReadyEvent.class)
    public void startProducing() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    sendProducerMessage();
                    // zufällig zwischen 1-5 Sekunden
                    int delay = random.nextInt(4000) + 1000;
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void sendProducerMessage() {
        double cloudCover = weatherService.getCloudCover();
        System.out.println(cloudCover);
        double maxKwh = 0.008 * (1 - cloudCover / 100.0);
        double kwh = maxKwh * random.nextDouble();
        kwh = Math.round(kwh * 10000.0) / 10000.0;

        String datetime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        String message = String.format(
                java.util.Locale.US,
                "{\"type\":\"PRODUCER\",\"association\":\"COMMUNITY\"," +
                        "\"kwh\":%.4f,\"datetime\":\"%s\"}",
                kwh, datetime);

        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);
        System.out.println("Sent: " + message);
    }
}