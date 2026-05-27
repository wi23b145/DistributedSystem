package at.fhtw.energy.energyapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "percentage_data")
public class PercentageDataEntity {

    @Id
    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "community_depleted")
    private double communityDepleted;

    @Column(name = "grid_portion")
    private double gridPortion;

    public LocalDateTime getHour() { return hour; }
    public double getCommunityDepleted() { return communityDepleted; }
    public double getGridPortion() { return gridPortion; }

    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public void setCommunityDepleted(double communityDepleted) { this.communityDepleted = communityDepleted; }
    public void setGridPortion(double gridPortion) { this.gridPortion = gridPortion; }
}