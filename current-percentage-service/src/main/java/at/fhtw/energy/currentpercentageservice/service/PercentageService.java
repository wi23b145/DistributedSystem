package at.fhtw.energy.currentpercentageservice.service;

import at.fhtw.energy.currentpercentageservice.config.RabbitMQConfig;
import at.fhtw.energy.currentpercentageservice.entity.PercentageDataEntity;
import at.fhtw.energy.currentpercentageservice.entity.UsageDataEntity;
import at.fhtw.energy.currentpercentageservice.repository.PercentageDataRepository;
import at.fhtw.energy.currentpercentageservice.repository.UsageDataRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PercentageService {

    private final UsageDataRepository usageDataRepository;
    private final PercentageDataRepository percentageDataRepository;

    public PercentageService(UsageDataRepository usageDataRepository,
                             PercentageDataRepository percentageDataRepository) {
        this.usageDataRepository = usageDataRepository;
        this.percentageDataRepository = percentageDataRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.UPDATE_QUEUE)
    public void processUpdate(String hourStr) {
        try {
            LocalDateTime hour = LocalDateTime.parse(hourStr);

            Optional<UsageDataEntity> usageOpt = usageDataRepository.findById(hour);
            if (usageOpt.isEmpty()) return;

            UsageDataEntity usage = usageOpt.get();

            //Lokale Variablen
            double communityProduced = usage.getCommunityProduced();
            double communityUsed = usage.getCommunityUsed();
            double gridUsed = usage.getGridUsed();

            // community_depleted: wie viel % des Community Pools verbraucht wurde
            double communityDepleted = communityProduced > 0
                    ? (communityUsed / communityProduced) * 100.0
                    : 0.0;

            // grid_portion: wie viel % des gesamten Verbrauchs aus dem Grid kam
            double totalEnergy = communityUsed + gridUsed;
            double gridPortion = totalEnergy > 0
                    ? (gridUsed / totalEnergy) * 100.0
                    : 0.0;

            // Runden auf 2 Dezimalstellen
            communityDepleted = Math.round(communityDepleted * 100.0) / 100.0;
            gridPortion = Math.round(gridPortion * 100.0) / 100.0;

            // Tabelle leeren - nur aktuelle Stunde behalten (laut Spec)
            percentageDataRepository.deleteAll();

            PercentageDataEntity entity = new PercentageDataEntity();
            entity.setHour(hour);
            entity.setCommunityDepleted(communityDepleted);
            entity.setGridPortion(gridPortion);

            percentageDataRepository.save(entity);
            System.out.println("Saved percentage for hour: " + hour +
                    " | depleted: " + communityDepleted + "%" +
                    " | grid: " + gridPortion + "%");

        } catch (Exception e) {
            System.err.println("Error processing update: " + e.getMessage());
        }
    }
}