package ecostruxure.rate.calculator.be.dto;

import ecostruxure.rate.calculator.be.Geography;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TeamDTO {
    private UUID teamId;
    private String name;
    private BigDecimal markupPercentage;
    private BigDecimal totalCostWithMarkup;
    private BigDecimal grossMarginPercentage;
    private BigDecimal totalCostWithGrossMargin;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal totalAllocatedHours;
    private BigDecimal totalAllocatedCost;
    private LocalDate updatedAt;
    private boolean archived;
    private List<TeamProfileDTO> teamProfiles;
    private List<Geography> geographies;

    public TeamDTO(UUID teamId,String name, BigDecimal markupPercentage, BigDecimal totalCostWithMarkup,
                   BigDecimal grossMarginPercentage, BigDecimal totalCostWithGrossMargin, BigDecimal hourlyRate,
                   BigDecimal dayRate, BigDecimal totalAllocatedHours, BigDecimal totalAllocatedCost,
                   LocalDate updatedAt, boolean archived, List<TeamProfileDTO> teamProfiles, List<Geography> geographies) {
        this.teamId = teamId;
        this.name = name;
        this.markupPercentage = markupPercentage;
        this.totalCostWithMarkup = totalCostWithMarkup;
        this.grossMarginPercentage = grossMarginPercentage;
        this.totalCostWithGrossMargin = totalCostWithGrossMargin;
        this.hourlyRate = hourlyRate;
        this.dayRate = dayRate;
        this.totalAllocatedHours = totalAllocatedHours;
        this.totalAllocatedCost = totalAllocatedCost;
        this.updatedAt = updatedAt;
        this.archived = archived;
        this.teamProfiles = teamProfiles;
        this.geographies = geographies;
    }

    public TeamDTO() {
    }

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

    public BigDecimal getMarkupPercentage() {
        return markupPercentage;
    }

    public void setMarkupPercentage(BigDecimal markupPercentage) {
        this.markupPercentage = markupPercentage;
    }

    public BigDecimal getTotalCostWithMarkup() {
        return totalCostWithMarkup;
    }

    public void setTotalCostWithMarkup(BigDecimal totalCostWithMarkup) {
        this.totalCostWithMarkup = totalCostWithMarkup;
    }

    public BigDecimal getGrossMarginPercentage() {
        return grossMarginPercentage;
    }

    public void setGrossMarginPercentage(BigDecimal grossMarginPercentage) {
        this.grossMarginPercentage = grossMarginPercentage;
    }

    public BigDecimal getTotalCostWithGrossMargin() {
        return totalCostWithGrossMargin;
    }

    public void setTotalCostWithGrossMargin(BigDecimal totalCostWithGrossMargin) {
        this.totalCostWithGrossMargin = totalCostWithGrossMargin;
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

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<TeamProfileDTO> getTeamProfiles() {
        return teamProfiles;
    }

    public void setTeamProfiles(List<TeamProfileDTO> teamProfiles) {
        this.teamProfiles = teamProfiles;
    }

    public List<Geography> getGeographies() {
        return geographies;
    }

    public void setGeographies(List<Geography> geographies) {
        this.geographies = geographies;
    }
}
