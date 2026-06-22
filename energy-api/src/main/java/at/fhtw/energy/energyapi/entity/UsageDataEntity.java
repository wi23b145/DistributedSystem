package at.fhtw.energy.energyapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
// JPA Entity - maps to usage_data table (one row per hour, many rows total)
@Entity
@Table(name = "usage_data")
public class UsageDataEntity {
    // Primary Key - each hour is unique, used for hourly aggregation
    @Id
    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "community_produced")
    private double communityProduced;

    @Column(name = "community_used")
    private double communityUsed;

    @Column(name = "grid_used")
    private double gridUsed;

    // Getters used by Hibernate and EnergyService to read field values
    public LocalDateTime getHour() { return hour; }
    public double getCommunityProduced() { return communityProduced; }
    public double getCommunityUsed() { return communityUsed; }
    public double getGridUsed() { return gridUsed; }

    // Setters required by Hibernate to populate entity when reading from DB
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public void setCommunityProduced(double communityProduced) { this.communityProduced = communityProduced; }
    public void setCommunityUsed(double communityUsed) { this.communityUsed = communityUsed; }
    public void setGridUsed(double gridUsed) { this.gridUsed = gridUsed; }
}