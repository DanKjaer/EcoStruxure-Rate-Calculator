package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProfileHistory {
    private int historyId;
    private int profileId;
    private boolean overhead;
    private BigDecimal annualSalary;
    private BigDecimal effectiveness;
    private BigDecimal effectiveWorkHours;
    private BigDecimal hoursPerDay;
    private LocalDateTime updatedAt;

    public ProfileHistory() {
    }

    public ProfileHistory(int historyId, int profileId, boolean overhead, BigDecimal annualSalary, BigDecimal effectiveness, BigDecimal effectiveWorkHours, BigDecimal hoursPerDay, LocalDateTime updatedAt) {
        this.historyId = historyId;
        this.profileId = profileId;
        this.overhead = overhead;
        this.annualSalary = annualSalary;
        this.effectiveness = effectiveness;
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

    public BigDecimal effectiveness() {
        return effectiveness;
    }

    public void effectiveness(BigDecimal effectiveness) {
        this.effectiveness = effectiveness;
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
