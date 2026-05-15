package at.fhtw.energy.energyapi.repository;

import at.fhtw.energy.energyapi.entity.PercentageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PercentageDataRepository extends JpaRepository<PercentageDataEntity, LocalDateTime> {
    Optional<PercentageDataEntity> findTopByOrderByHourDesc();
}