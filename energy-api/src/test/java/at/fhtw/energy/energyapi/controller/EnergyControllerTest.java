package at.fhtw.energy.energyapi.controller;

import at.fhtw.energy.energyapi.dto.CurrentEnergyDto;
import at.fhtw.energy.energyapi.dto.HistoricalEnergyDto;
import at.fhtw.energy.energyapi.service.EnergyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// Tests only the web layer - no real DB or service needed
@WebMvcTest(EnergyController.class)
class EnergyControllerTest {

    @Autowired
    private MockMvc mockMvc; // simulates HTTP requests without starting a real server

    @MockitoBean
    private EnergyService energyService; // mocked - returns predefined values instead of hitting DB

    // Tests that GET /energy/current returns HTTP 200 and correct percentage values
    @Test
    void getCurrent_returns200_withData() throws Exception {
        when(energyService.getCurrentEnergy())
                .thenReturn(new CurrentEnergyDto("2026-05-15T14:00", 75.5, 12.3));

        mockMvc.perform(get("/energy/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.communityDepleted").value(75.5))
                .andExpect(jsonPath("$.gridPortion").value(12.3))
                .andExpect(jsonPath("$.hour").value("2026-05-15T14:00"));
    }

    // Tests that GET /energy/current still returns HTTP 200 when DB is empty (fallback DTO)
    @Test
    void getCurrent_returns200_withNoData() throws Exception {
        when(energyService.getCurrentEnergy())
                .thenReturn(new CurrentEnergyDto("No data", 0, 0));

        mockMvc.perform(get("/energy/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hour").value("No data"))
                .andExpect(jsonPath("$.communityDepleted").value(0))
                .andExpect(jsonPath("$.gridPortion").value(0));
    }

    // Tests that GET /energy/historical returns HTTP 200 and correct kWh values for given date range
    @Test
    void getHistorical_returns200_withData() throws Exception {
        when(energyService.getHistoricalEnergy("2026-05-15T00:00:00", "2026-05-15T23:00:00"))
                .thenReturn(List.of(new HistoricalEnergyDto("2026-05-15T14:00", 1.5, 1.2, 0.1)));

        mockMvc.perform(get("/energy/historical")
                        .param("start", "2026-05-15T00:00:00")
                        .param("end", "2026-05-15T23:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].communityProduced").value(1.5))
                .andExpect(jsonPath("$[0].communityUsed").value(1.2))
                .andExpect(jsonPath("$[0].gridUsed").value(0.1));
    }

    // Tests that GET /energy/historical returns HTTP 200 and empty array when no data exists for range
    @Test
    void getHistorical_returns200_withEmptyList() throws Exception {
        when(energyService.getHistoricalEnergy("2026-01-01T00:00:00", "2026-01-01T23:00:00"))
                .thenReturn(List.of());

        mockMvc.perform(get("/energy/historical")
                        .param("start", "2026-01-01T00:00:00")
                        .param("end", "2026-01-01T23:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}