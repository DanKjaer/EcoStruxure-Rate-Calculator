package ecostruxure.rate.calculator.be;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
public class Team {

    @Id
    @GeneratedValue
    private UUID teamId;

    @Column(nullable = false)
    private String name;

    @Column(precision = 15, scale = 2)
    private BigDecimal markup;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalMarkup;

    @Column(precision = 15, scale = 2)
    private BigDecimal grossMargin;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalGrossMargin;

    @Column(precision = 15, scale = 2)
    private BigDecimal hourlyRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal dayRate;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAllocatedHours;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalAllocatedCost;

    private Timestamp updatedAt;

    private boolean archived;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamProfile> teamProfiles;

    @ManyToMany
    @JoinTable(
            name = "team_geography",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "geography_id")
    )
    private List<Geography> geographies;

    // Getters and Setters
    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMarkup() {
        return markup;
    }

    public void setMarkup(BigDecimal markup) {
        this.markup = markup;
    }

    public BigDecimal getTotalMarkup() {
        return totalMarkup;
    }

    public void setTotalMarkup(BigDecimal totalMarkup) {
        this.totalMarkup = totalMarkup;
    }

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
    }

    public BigDecimal getTotalGrossMargin() {
        return totalGrossMargin;
    }

    public void setTotalGrossMargin(BigDecimal totalGrossMargin) {
        this.totalGrossMargin = totalGrossMargin;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal getTotalAllocatedHours() {
        return totalAllocatedHours;
    }

    public void setTotalAllocatedHours(BigDecimal totalAllocatedHours) {
        this.totalAllocatedHours = totalAllocatedHours;
    }

    public BigDecimal getTotalAllocatedCost() {
        return totalAllocatedCost;
    }

    public void setTotalAllocatedCost(BigDecimal totalAllocatedCost) {
        this.totalAllocatedCost = totalAllocatedCost;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<TeamProfile> getTeamProfiles() {
        return teamProfiles;
    }

    public void setTeamProfiles(List<TeamProfile> teamProfiles) {
        this.teamProfiles = teamProfiles;
    }

    public List<Geography> getGeographies() {
        return geographies;
    }

    public void setGeographies(List<Geography> geographies) {
        this.geographies = geographies;
    }
}
