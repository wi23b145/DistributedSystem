package at.fhtw.energy.energyapi.service;

import at.fhtw.energy.energyapi.dto.CurrentEnergyDto;
import at.fhtw.energy.energyapi.dto.HistoricalEnergyDto;
import at.fhtw.energy.energyapi.entity.PercentageDataEntity;
import at.fhtw.energy.energyapi.entity.UsageDataEntity;
import at.fhtw.energy.energyapi.repository.PercentageDataRepository;
import at.fhtw.energy.energyapi.repository.UsageDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnergyServiceTest {

    @Mock
    private PercentageDataRepository percentageDataRepository;

    @Mock
    private UsageDataRepository usageDataRepository;

    @InjectMocks
    private EnergyService energyService;

    @Test
    void getCurrentEnergy_returnsData_whenEntryExists() {
        PercentageDataEntity entity = new PercentageDataEntity();
        entity.setHour(LocalDateTime.of(2026, 5, 15, 14, 0));
        entity.setCommunityDepleted(75.5);
        entity.setGridPortion(12.3);

        when(percentageDataRepository.findAll()).thenReturn(List.of(entity));

        CurrentEnergyDto result = energyService.getCurrentEnergy();

        assertThat(result.getCommunityDepleted()).isEqualTo(75.5);
        assertThat(result.getGridPortion()).isEqualTo(12.3);
        assertThat(result.getHour()).isEqualTo("2026-05-15T14:00");
    }

    @Test
    void getCurrentEnergy_returnsNoData_whenTableEmpty() {
        when(percentageDataRepository.findAll()).thenReturn(List.of());

        CurrentEnergyDto result = energyService.getCurrentEnergy();

        assertThat(result.getHour()).isEqualTo("No data");
        assertThat(result.getCommunityDepleted()).isEqualTo(0);
        assertThat(result.getGridPortion()).isEqualTo(0);
    }

    @Test
    void getHistoricalEnergy_returnsData_whenEntriesExist() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 15, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 15, 23, 0);

        UsageDataEntity entity = new UsageDataEntity();
        entity.setHour(LocalDateTime.of(2026, 5, 15, 14, 0));
        entity.setCommunityProduced(1.5);
        entity.setCommunityUsed(1.2);
        entity.setGridUsed(0.1);

        when(usageDataRepository.findByHourBetween(start, end)).thenReturn(List.of(entity));

        List<HistoricalEnergyDto> result = energyService.getHistoricalEnergy(
                "2026-05-15T00:00:00",
                "2026-05-15T23:00:00"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCommunityProduced()).isEqualTo(1.5);
        assertThat(result.get(0).getCommunityUsed()).isEqualTo(1.2);
        assertThat(result.get(0).getGridUsed()).isEqualTo(0.1);
    }

    @Test
    void getHistoricalEnergy_returnsEmptyList_whenNoEntriesExist() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 1, 23, 0);

        when(usageDataRepository.findByHourBetween(start, end)).thenReturn(List.of());

        List<HistoricalEnergyDto> result = energyService.getHistoricalEnergy(
                "2026-01-01T00:00:00",
                "2026-01-01T23:00:00"
        );

        assertThat(result).isEmpty();
    }
}