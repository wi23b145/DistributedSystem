package at.fhtw.energy.energyapi.controller;

import at.fhtw.energy.energyapi.dto.CurrentEnergyDto;
import at.fhtw.energy.energyapi.dto.HistoricalEnergyDto;
import at.fhtw.energy.energyapi.service.EnergyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
// REST Controller - handles HTTP requests, delegates logic to EnergyService
@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyService energyService;

    // Dependency Injection via constructor
    public EnergyController(EnergyService energyService) {

        this.energyService = energyService;
    }

    // GET /energy/current - returns latest community pool and grid portion %
    @GetMapping("/current")
    public CurrentEnergyDto getCurrent() {

        return energyService.getCurrentEnergy();
    }

    // GET /energy/historical?start=...&end=... - returns hourly kWh data for selected range
    @GetMapping("/historical")
    public List<HistoricalEnergyDto> getHistorical(
            @RequestParam String start,
            @RequestParam String end) {
        return energyService.getHistoricalEnergy(start, end);
    }
}






