package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProfileHistory {
    private int historyId;
    private int profileId;
    private boolean overhead;
    private BigDecimal annualSalary;
    private BigDecimal fixedAnnualAmount;
    private BigDecimal overheadMultiplier;
    private BigDecimal effectiveWorkHours;
    private BigDecimal hoursPerDay;
    private LocalDateTime updatedAt;

    public ProfileHistory() {
    }

    public ProfileHistory(int historyId, int profileId, boolean overhead, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal effectiveWorkHours, BigDecimal hoursPerDay, LocalDateTime updatedAt) {
        this.historyId = historyId;
        this.profileId = profileId;
        this.overhead = overhead;
        this.annualSalary = annualSalary;
        this.fixedAnnualAmount = fixedAnnualAmount;
        this.overheadMultiplier = overheadMultiplier;
        this.effectiveWorkHours = effectiveWorkHours;
        this.hoursPerDay = hoursPerDay;
        this.updatedAt = updatedAt;
    }

    public int historyId() {
        return historyId;
    }

    public void historyId(int historyId) {
        this.historyId = historyId;
    }

    public int profileId() {
        return profileId;
    }

    public void profileId(int profileId) {
        this.profileId = profileId;
    }

    public boolean overhead() {
        return overhead;
    }

    public void overhead(boolean overhead) {
        this.overhead = overhead;
    }

    public BigDecimal annualSalary() {
        return annualSalary;
    }

    public void annualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal fixedAnnualAmount() {
        return fixedAnnualAmount;
    }

    public void fixedAnnualAmount(BigDecimal fixedAnnualAmount) {
        this.fixedAnnualAmount = fixedAnnualAmount;
    }

    public BigDecimal overheadMultiplier() {
        return overheadMultiplier;
    }

    public void overheadMultiplier(BigDecimal overheadMultiplier) {
        this.overheadMultiplier = overheadMultiplier;
    }

    public BigDecimal effectiveWorkHours() {
        return effectiveWorkHours;
    }

    public void effectiveWorkHours(BigDecimal effectiveWorkHours) {
        this.effectiveWorkHours = effectiveWorkHours;
    }

    public BigDecimal hoursPerDay() {
        return hoursPerDay;
    }

    public void hoursPerDay(BigDecimal hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public void updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
