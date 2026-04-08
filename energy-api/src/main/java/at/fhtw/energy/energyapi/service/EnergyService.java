package at.fhtw.energy.energyapi.service;

import at.fhtw.energy.energyapi.dto.CurrentEnergyDto;
import at.fhtw.energy.energyapi.dto.HistoricalEnergyDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnergyService {

    public CurrentEnergyDto getCurrentEnergy() {
        return new CurrentEnergyDto("2025-01-10T14:00:00", 78.54, 7.23);
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(String start, String end) {
        return List.of(
                new HistoricalEnergyDto("2025-01-10T14:00:00", 143.024, 130.101, 14.75),
                new HistoricalEnergyDto("2025-01-10T13:00:00", 120.5, 110.3, 10.2)
        );
    }
}