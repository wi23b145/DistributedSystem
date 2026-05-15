package at.fhtw.energy.currentpercentageservice.repository;

import at.fhtw.energy.currentpercentageservice.entity.PercentageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface PercentageDataRepository extends JpaRepository<PercentageDataEntity, LocalDateTime> {
}