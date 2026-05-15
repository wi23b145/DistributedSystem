package at.fhtw.energy.energyapi.repository;

import at.fhtw.energy.energyapi.entity.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {
    List<UsageDataEntity> findByHourBetween(LocalDateTime start, LocalDateTime end);
}