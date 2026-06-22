package at.fhtw.energy.currentpercentageservice.repository;

import at.fhtw.energy.currentpercentageservice.entity.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {
}
