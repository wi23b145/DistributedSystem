package at.fhtw.energy.energyuser.service;

import at.fhtw.energy.energyuser.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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

    @EventListener(ApplicationReadyEvent.class)
    public void startConsuming() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    sendUserMessage();
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

    private void sendUserMessage() {
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