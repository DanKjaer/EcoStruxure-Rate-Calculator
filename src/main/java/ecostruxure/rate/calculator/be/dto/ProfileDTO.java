package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ProfileDTO {
    private String ProfileId;
    private String name;
    private GeographyDTO geography;
    private boolean resourceType;
    private BigDecimal annualCost;
    private BigDecimal annualHours;
    private BigDecimal hoursPerDay;
    private BigDecimal effectivenessPercentage;
    private BigDecimal effectiveWorkHours;
    private BigDecimal totalCostAllocation;
    private BigDecimal totalHourAllocation;
    private boolean archived;
    private Timestamp updatedAt;

    public ProfileDTO(String profileId,
                      String name,
                      String currency,
                      GeographyDTO geography,
                      boolean resourceType,
                      BigDecimal annualCost,
                      BigDecimal annualHours,
                      BigDecimal hoursPerDay,
                      BigDecimal effectivenessPercentage,
                      BigDecimal effectiveWorkHours,
                      BigDecimal totalCostAllocation,
                      BigDecimal totalHourAllocation,
                      boolean archived,
                      Timestamp updatedAt) {
        ProfileId = profileId;
        this.name = name;
        this.geography = geography;
        this.resourceType = resourceType;
        this.annualCost = annualCost;
        this.annualHours = annualHours;
        this.hoursPerDay = hoursPerDay;
        this.effectivenessPercentage = effectivenessPercentage;
        this.effectiveWorkHours = effectiveWorkHours;
        this.totalCostAllocation = totalCostAllocation;
        this.totalHourAllocation = totalHourAllocation;
        this.archived = archived;
        this.updatedAt = updatedAt;
    }

    public ProfileDTO() {
    }

    public String getProfileId() {
        return ProfileId;
    }

    public void setProfileId(String profileId) {
        ProfileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeographyDTO getGeography() {
        return geography;
    }

    public void setGeography(GeographyDTO geography) {
        this.geography = geography;
    }

    public boolean isResourceType() {
        return resourceType;
    }

    public void setResourceType(boolean resourceType) {
        this.resourceType = resourceType;
    }

    public BigDecimal getAnnualCost() {
        return annualCost;
    }

    public void setAnnualCost(BigDecimal annualCost) {
        this.annualCost = annualCost;
    }

    public BigDecimal getAnnualHours() {
        return annualHours;
    }

    public void setAnnualHours(BigDecimal annualHours) {
        this.annualHours = annualHours;
    }

    public BigDecimal getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(BigDecimal hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public BigDecimal getEffectivenessPercentage() {
        return effectivenessPercentage;
    }

    public void setEffectivenessPercentage(BigDecimal effectivenessPercentage) {
        this.effectivenessPercentage = effectivenessPercentage;
    }

    public BigDecimal getEffectiveWorkHours() {
        return effectiveWorkHours;
    }

    public void setEffectiveWorkHours(BigDecimal effectiveWorkHours) {
        this.effectiveWorkHours = effectiveWorkHours;
    }

    public BigDecimal getTotalCostAllocation() {
        return totalCostAllocation;
    }

    public void setTotalCostAllocation(BigDecimal totalCostAllocation) {
        this.totalCostAllocation = totalCostAllocation;
    }

    public BigDecimal getTotalHourAllocation() {
        return totalHourAllocation;
    }

    public void setTotalHourAllocation(BigDecimal totalHourAllocation) {
        this.totalHourAllocation = totalHourAllocation;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
