package at.fhtw.energy.usageservice.repository;

import at.fhtw.energy.usageservice.entity.UsageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface UsageDataRepository extends JpaRepository<UsageDataEntity, LocalDateTime> {
}