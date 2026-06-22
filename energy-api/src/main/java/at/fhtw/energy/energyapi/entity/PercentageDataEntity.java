package at.fhtw.energy.energyapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// JPA Entity - maps to percentage_data table
@Entity
@Table(name = "percentage_data")
public class PercentageDataEntity {
    // Primary Key - each hour is unique
    @Id
    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "community_depleted")
    private double communityDepleted;

    @Column(name = "grid_portion")
    private double gridPortion;

    // Getters used by Jackson and Hibernate to read field values
    public LocalDateTime getHour() { return hour; }
    public double getCommunityDepleted() { return communityDepleted; }
    public double getGridPortion() { return gridPortion; }

    // Setters required by Hibernate to populate entity when reading from DB
    public void setHour(LocalDateTime hour) { this.hour = hour; }
    public void setCommunityDepleted(double communityDepleted) { this.communityDepleted = communityDepleted; }
    public void setGridPortion(double gridPortion) { this.gridPortion = gridPortion; }
}