package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TeamProfileHistory {
    private int profileId;
    private int profileHistoryId;
    private BigDecimal utilizationRate;
    private BigDecimal utilizationHours;
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

    public BigDecimal utilizationRate() {
        return utilizationRate;
    }

    public void utilizationRate(BigDecimal utilizationRate) {
        this.utilizationRate = utilizationRate;
    }

    public BigDecimal utilizationHours() {
        return utilizationHours;
    }

    public void utilizationHours(BigDecimal utilizationHours) {
        this.utilizationHours = utilizationHours;
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
                ", utilizationRate=" + utilizationRate +
                ", utilizationHours=" + utilizationHours +
                ", hourlyRate=" + hourlyRate +
                ", dayRate=" + dayRate +
                ", annualCost=" + annualCost +
                ", totalHours=" + totalHours +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
