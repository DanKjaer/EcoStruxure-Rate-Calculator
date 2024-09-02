package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TeamProfileHistory {
    private UUID profileId;
    private UUID profileHistoryId;
    private BigDecimal costAllocation;
    private BigDecimal hourAllocation;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal annualCost;
    private BigDecimal annualHours;
    private LocalDateTime updatedAt;

    public UUID profileId() {
        return profileId;
    }

    public void profileId(UUID profileId) {
        this.profileId = profileId;
    }

    public UUID profileHistoryId() {
        return profileHistoryId;
    }

    public void profileHistoryId(UUID profileHistoryId) {
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

    public BigDecimal annualHours() {
        return annualHours;
    }

    public void annualHours(BigDecimal totalHours) {
        this.annualHours = totalHours;
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
                ", annualHours=" + annualHours +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
