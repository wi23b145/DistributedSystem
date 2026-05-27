package at.fhtw.energy.usageservice.service;

import at.fhtw.energy.usageservice.entity.UsageDataEntity;
import at.fhtw.energy.usageservice.repository.UsageDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageServiceTest {

    @Mock
    private UsageDataRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UsageService usageService;

    @Test
    void processMessage_producer_increasesCommunityProduced() {
        String message = """
                {"type":"PRODUCER","association":"COMMUNITY","kwh":0.005,"datetime":"2026-05-15T14:30:00"}
                """;

        when(repository.findById(any())).thenReturn(Optional.empty());

        usageService.processMessage(message);

        ArgumentCaptor<UsageDataEntity> captor = ArgumentCaptor.forClass(UsageDataEntity.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getCommunityProduced()).isEqualTo(0.005);
        assertThat(captor.getValue().getCommunityUsed()).isEqualTo(0.0);
        assertThat(captor.getValue().getGridUsed()).isEqualTo(0.0);
    }

    @Test
    void processMessage_user_usesCommunityFirst_whenEnoughAvailable() {
        String message = """
                {"type":"USER","association":"COMMUNITY","kwh":0.003,"datetime":"2026-05-15T14:30:00"}
                """;

        // Community hat 0.01 produziert, 0.002 verbraucht → 0.008 verfügbar
        UsageDataEntity existing = new UsageDataEntity();
        existing.setHour(LocalDateTime.of(2026, 5, 15, 14, 0));
        existing.setCommunityProduced(0.01);
        existing.setCommunityUsed(0.002);
        existing.setGridUsed(0.0);

        when(repository.findById(any())).thenReturn(Optional.of(existing));

        usageService.processMessage(message);

        ArgumentCaptor<UsageDataEntity> captor = ArgumentCaptor.forClass(UsageDataEntity.class);
        verify(repository).save(captor.capture());

        // 0.003 kommt komplett aus Community
        assertThat(captor.getValue().getCommunityUsed()).isEqualTo(0.005);
        assertThat(captor.getValue().getGridUsed()).isEqualTo(0.0);
    }

    @Test
    void processMessage_user_usesGridWhenCommunityPoolEmpty() {
        String message = """
                {"type":"USER","association":"COMMUNITY","kwh":0.005,"datetime":"2026-05-15T14:30:00"}
                """;

        // Community hat 0.01 produziert, bereits 0.01 verbraucht → kein Pool mehr
        UsageDataEntity existing = new UsageDataEntity();
        existing.setHour(LocalDateTime.of(2026, 5, 15, 14, 0));
        existing.setCommunityProduced(0.01);
        existing.setCommunityUsed(0.01);
        existing.setGridUsed(0.0);

        when(repository.findById(any())).thenReturn(Optional.of(existing));

        usageService.processMessage(message);

        ArgumentCaptor<UsageDataEntity> captor = ArgumentCaptor.forClass(UsageDataEntity.class);
        verify(repository).save(captor.capture());

        // community_used bleibt gleich (Pool leer), alles geht auf Grid
        assertThat(captor.getValue().getCommunityUsed()).isEqualTo(0.01);
        assertThat(captor.getValue().getGridUsed()).isEqualTo(0.005);
    }

    @Test
    void processMessage_user_splitsBetweenCommunityAndGrid() {
        String message = """
                {"type":"USER","association":"COMMUNITY","kwh":0.008,"datetime":"2026-05-15T14:30:00"}
                """;

        // Community hat 0.01 produziert, 0.007 verbraucht → nur 0.003 verfügbar
        UsageDataEntity existing = new UsageDataEntity();
        existing.setHour(LocalDateTime.of(2026, 5, 15, 14, 0));
        existing.setCommunityProduced(0.01);
        existing.setCommunityUsed(0.007);
        existing.setGridUsed(0.0);

        when(repository.findById(any())).thenReturn(Optional.of(existing));

        usageService.processMessage(message);

        ArgumentCaptor<UsageDataEntity> captor = ArgumentCaptor.forClass(UsageDataEntity.class);
        verify(repository).save(captor.capture());

        // 0.003 aus Community, 0.005 aus Grid
        assertThat(captor.getValue().getCommunityUsed()).isEqualTo(0.01);
        assertThat(captor.getValue().getGridUsed()).isEqualTo(0.005);
    }

    @Test
    void processMessage_communityUsed_neverExceedsCommunityProduced() {
        String message = """
                {"type":"USER","association":"COMMUNITY","kwh":0.1,"datetime":"2026-05-15T14:30:00"}
                """;

        UsageDataEntity existing = new UsageDataEntity();
        existing.setHour(LocalDateTime.of(2026, 5, 15, 14, 0));
        existing.setCommunityProduced(0.01);
        existing.setCommunityUsed(0.0);
        existing.setGridUsed(0.0);

        when(repository.findById(any())).thenReturn(Optional.of(existing));

        usageService.processMessage(message);

        ArgumentCaptor<UsageDataEntity> captor = ArgumentCaptor.forClass(UsageDataEntity.class);
        verify(repository).save(captor.capture());

        // community_used darf nie > community_produced sein!
        assertThat(captor.getValue().getCommunityUsed())
                .isLessThanOrEqualTo(captor.getValue().getCommunityProduced());
    }

    @Test
    void processMessage_nonCommunity_isIgnored() {
        String message = """
                {"type":"PRODUCER","association":"GRID","kwh":0.005,"datetime":"2026-05-15T14:30:00"}
                """;

        usageService.processMessage(message);

        // Nichts wird gespeichert bei non-COMMUNITY
        verify(repository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void processMessage_notifiesPercentageService_afterSave() {
        String message = """
                {"type":"PRODUCER","association":"COMMUNITY","kwh":0.005,"datetime":"2026-05-15T14:30:00"}
                """;

        when(repository.findById(any())).thenReturn(Optional.empty());

        usageService.processMessage(message);

        // Update Queue wird benachrichtigt
        verify(rabbitTemplate).convertAndSend(anyString(), (Object) eq("2026-05-15T14:00"));
    }
}