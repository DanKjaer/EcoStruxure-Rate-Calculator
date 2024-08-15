package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TeamProfileHistory {
    private int profileId;
    private int profileHistoryId;
    private BigDecimal costAllocation;
    private BigDecimal hourAllocation;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal annualCost;
    private BigDecimal totalHours;
    private LocalDateTime updatedAt;

    public int profileId() {
        return profileId;
    }

    public void profileId(int profileId) {
        this.profileId = profileId;
    }

    public int profileHistoryId() {
        return profileHistoryId;
    }

    public void profileHistoryId(int profileHistoryId) {
        this.profileHistoryId = profileHistoryId;
    }

    public BigDecimal costAllocation() {
        return costAllocation;
    }

    public void costAllocation(BigDecimal costAllocation) {
        this.costAllocation = costAllocation;
    }

    public BigDecimal hourAllocation() {
        return hourAllocation;
    }

    public void hourAllocation(BigDecimal hourAllocation) {
        this.hourAllocation = hourAllocation;
    }

    public BigDecimal hourlyRate() {
        return hourlyRate;
    }

    public void hourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal dayRate() {
        return dayRate;
    }

    public void dayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal annualCost() {
        return annualCost;
    }

    public void annualCost(BigDecimal annualCost) {
        this.annualCost = annualCost;
    }

    public BigDecimal totalHours() {
        return totalHours;
    }

    public void totalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public LocalDateTime updatedAt() {
        return updatedAt;
    }

    public void updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "TeamProfileHistory{" +
                "profileId=" + profileId +
                ", profileHistoryId=" + profileHistoryId +
                ", costAllocation=" + costAllocation +
                ", hourAllocation=" + hourAllocation +
                ", hourlyRate=" + hourlyRate +
                ", dayRate=" + dayRate +
                ", annualCost=" + annualCost +
                ", totalHours=" + totalHours +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
