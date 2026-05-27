package at.fhtw.energy.energyapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_data")
public class UsageDataEntity {

    @Id
    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "community_produced")
    private double communityProduced;

    @Column(name = "community_used")
    private double communityUsed;

    @Column(name = "grid_used")
    private double gridUsed;

    public LocalDateTime getHour() { return hour; }
    public double getCommunityProduced() { return communityProduced; }
    public double getCommunityUsed() { return communityUsed; }
    public double getGridUsed() { return gridUsed; }

    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public void setCommunityProduced(double communityProduced) { this.communityProduced = communityProduced; }
    public void setCommunityUsed(double communityUsed) { this.communityUsed = communityUsed; }
    public void setGridUsed(double gridUsed) { this.gridUsed = gridUsed; }
}