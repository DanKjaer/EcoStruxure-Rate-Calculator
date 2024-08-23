package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ProfileHistory {
    private int historyId;
    private UUID profileId;
    private boolean resourceType;
    private BigDecimal annualSalary;
    private BigDecimal effectiveness;
    private BigDecimal annualHours;
    private BigDecimal effectiveWorkHours;
    private BigDecimal hoursPerDay;
    private LocalDateTime updatedAt;

    public ProfileHistory() {
    }

    public ProfileHistory(int historyId, UUID profileId, boolean resourceType, BigDecimal annualSalary, BigDecimal effectiveness, BigDecimal annualHours, BigDecimal effectiveWorkHours, BigDecimal hoursPerDay, LocalDateTime updatedAt) {
        this.historyId = historyId;
        this.profileId = profileId;
        this.resourceType = resourceType;
        this.annualSalary = annualSalary;
        this.effectiveness = effectiveness;
        this.annualHours = annualHours;
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

    public UUID profileId() {
        return profileId;
    }

    public void profileId(UUID profileId) {
        this.profileId = profileId;
    }

    public boolean resourceType() {
        return resourceType;
    }

    public void resourceType(boolean resourceType) {
        this.resourceType = resourceType;
    }

    public BigDecimal annualSalary() {
        return annualSalary;
    }

    public void annualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal effectiveness() {
        return effectiveness;
    }

    public void effectiveness(BigDecimal effectiveness) {
        this.effectiveness = effectiveness;
    }

    public BigDecimal annualHours() {
        return annualHours;
    }

    public void annualHours(BigDecimal annualHours) {
        this.annualHours = annualHours;
    }

    public BigDecimal effectiveWorkHours() {
        return effectiveWorkHours;
    }

    public void EffectiveWorkHours() {
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
