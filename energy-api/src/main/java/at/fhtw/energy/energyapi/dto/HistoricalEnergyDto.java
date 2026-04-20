
package at.fhtw.energy.energyapi.dto;

public class HistoricalEnergyDto {
    private String hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public HistoricalEnergyDto(String hour, double communityProduced,
                               double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getHour() { return hour; }
    public double getCommunityProduced() { return communityProduced; }
    public double getCommunityUsed() { return communityUsed; }
    public double getGridUsed() { return gridUsed; }
}
