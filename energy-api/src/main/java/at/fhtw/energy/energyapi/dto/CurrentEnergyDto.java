package at.fhtw.energy.energyapi.dto;

public class CurrentEnergyDto {
    private String hour;
    private double communityDepleted;
    private double gridPortion;

    public CurrentEnergyDto(String hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public String getHour() { return hour; }
    public double getCommunityDepleted() { return communityDepleted; }
    public double getGridPortion() { return gridPortion; }
}
