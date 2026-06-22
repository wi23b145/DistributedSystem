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
// Business logic layer - reads from DB and maps entities to DTOs
@Service
public class EnergyService {

    private final PercentageDataRepository percentageDataRepository;
    private final UsageDataRepository usageDataRepository;

    public EnergyService(PercentageDataRepository percentageDataRepository,
                         UsageDataRepository usageDataRepository) {
        this.percentageDataRepository = percentageDataRepository;
        this.usageDataRepository = usageDataRepository;
    }
    // Returns current community pool % and grid portion %
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
    // Returns hourly kWh data for selected date range
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