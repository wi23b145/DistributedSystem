package at.fhtw.energy.energygui.dto;
// DTO for GET /energy/historical
public class HistoricalEnergyDto {
    public String hour;
    public double communityProduced;
    public double communityUsed;
    public double gridUsed;
}
