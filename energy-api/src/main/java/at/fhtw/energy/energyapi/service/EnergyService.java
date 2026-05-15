package at.fhtw.energy.energyapi.service;

import at.fhtw.energy.energyapi.dto.CurrentEnergyDto;
import at.fhtw.energy.energyapi.dto.HistoricalEnergyDto;
import at.fhtw.energy.energyapi.entity.PercentageDataEntity;
import at.fhtw.energy.energyapi.entity.UsageDataEntity;
import at.fhtw.energy.energyapi.repository.PercentageDataRepository;
import at.fhtw.energy.energyapi.repository.UsageDataRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnergyService {

    private final PercentageDataRepository percentageDataRepository;
    private final UsageDataRepository usageDataRepository;

    public EnergyService(PercentageDataRepository percentageDataRepository,
                         UsageDataRepository usageDataRepository) {
        this.percentageDataRepository = percentageDataRepository;
        this.usageDataRepository = usageDataRepository;
    }

    public CurrentEnergyDto getCurrentEnergy() {
        // Tabelle hat laut Spec immer nur 1 Zeile - einfach den ersten Eintrag holen
        return percentageDataRepository.findAll()
                .stream()
                .findFirst()
                .map(e -> new CurrentEnergyDto(
                        e.getHour().toString(),
                        e.getCommunityDepleted(),
                        e.getGridPortion()))
                .orElse(new CurrentEnergyDto("No data", 0, 0));
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(String start, String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);

        return usageDataRepository.findByHourBetween(startTime, endTime)
                .stream()
                .map(e -> new HistoricalEnergyDto(
                        e.getHour().toString(),
                        e.getCommunityProduced(),
                        e.getCommunityUsed(),
                        e.getGridUsed()))
                .toList();
    }
}