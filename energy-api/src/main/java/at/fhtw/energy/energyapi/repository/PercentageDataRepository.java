package at.fhtw.energy.energyapi.repository;

import at.fhtw.energy.energyapi.entity.PercentageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
// Repository for percentage_data table - used by EnergyService for /energy/current
@Repository
public interface PercentageDataRepository extends JpaRepository<PercentageDataEntity, LocalDateTime> {
    // returns the most recent entry - Optional because table could be empty on first start
    Optional<PercentageDataEntity> findTopByOrderByHourDesc();
}