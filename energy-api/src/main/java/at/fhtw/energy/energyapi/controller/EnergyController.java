package at.fhtw.energy.energyapi.controller;

import at.fhtw.energy.energyapi.dto.CurrentEnergyDto;
import at.fhtw.energy.energyapi.dto.HistoricalEnergyDto;
import at.fhtw.energy.energyapi.service.EnergyService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/current")
    public CurrentEnergyDto getCurrent() {
        return energyService.getCurrentEnergy();
    }

    @GetMapping("/historical")
    public List<HistoricalEnergyDto> getHistorical(
            @RequestParam String start,
            @RequestParam String end) {
        return energyService.getHistoricalEnergy(start, end);
    }
}