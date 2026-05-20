package at.fhtw.energy.usageservice.service;

import at.fhtw.energy.usageservice.config.RabbitMQConfig;
import at.fhtw.energy.usageservice.entity.UsageDataEntity;
import at.fhtw.energy.usageservice.repository.UsageDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageService {

    private final UsageDataRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UsageService(UsageDataRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.ENERGY_QUEUE)
    public void processMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String type = json.get("type").asText();
            String association = json.get("association").asText();
            double kwh = json.get("kwh").asDouble();
            String datetimeStr = json.get("datetime").asText();

            if (!"COMMUNITY".equals(association)) return;

            LocalDateTime datetime = LocalDateTime.parse(datetimeStr);
            LocalDateTime hour = datetime.withMinute(0).withSecond(0).withNano(0);

            UsageDataEntity entity = repository.findById(hour)
                    .orElseGet(() -> {
                        UsageDataEntity e = new UsageDataEntity();
                        e.setHour(hour);
                        e.setCommunityProduced(0);
                        e.setCommunityUsed(0);
                        e.setGridUsed(0);
                        return e;
                    });

            if ("PRODUCER".equals(type)) {
                entity.setCommunityProduced(entity.getCommunityProduced() + kwh);
            } else if ("USER".equals(type)) {
                double available = entity.getCommunityProduced() - entity.getCommunityUsed();
                if (kwh <= available) {
                    entity.setCommunityUsed(entity.getCommunityUsed() + kwh);
                } else {
                    double fromCommunity = available;
                    double fromGrid = kwh - fromCommunity;
                    entity.setCommunityUsed(entity.getCommunityUsed() + fromCommunity);
                    entity.setGridUsed(entity.getGridUsed() + fromGrid);
                }
            }

            repository.save(entity);
            System.out.println("Updated usage for hour: " + hour);

            // Notify percentage service
            rabbitTemplate.convertAndSend(RabbitMQConfig.UPDATE_QUEUE, hour.toString());

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }
}