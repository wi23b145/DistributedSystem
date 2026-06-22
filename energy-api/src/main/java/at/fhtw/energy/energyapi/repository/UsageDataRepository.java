package at.fhtw.energy.energyapi.repository;

import at.fhtw.energy.energyapi.entity.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
// Repository for usage_data table - used by EnergyService for /energy/historical
@Repository
public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {
    // Spring auto-generates: SELECT * FROM usage_data WHERE hour BETWEEN start AND end
    List<UsageDataEntity> findByHourBetween(LocalDateTime start, LocalDateTime end);
}