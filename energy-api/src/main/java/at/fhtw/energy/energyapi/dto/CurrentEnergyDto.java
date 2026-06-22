package at.fhtw.energy.energyapi.dto;

// DTO for GET /energy/current - carries current community pool and grid portion %
public class CurrentEnergyDto {
    private String hour;
    private double communityDepleted;
    private double gridPortion;

    public CurrentEnergyDto(String hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    // Getters used by Jackson to serialize this DTO to JSON
    public String getHour() {
        return hour;
    }
    public double getCommunityDepleted() {
        return communityDepleted;
    }
    public double getGridPortion() {
        return gridPortion;
    }
}
