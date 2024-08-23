package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamHistory {
    public enum Reason {
        TEAM_CREATED,
        ASSIGNED_PROFILE,
        REMOVED_PROFILE,
        UPDATED_PROFILE,
        UTILIZATION_CHANGE,
    }

    private UUID teamId;
    private List<TeamProfileHistory> teamProfileHistories = new ArrayList<>();
    private Reason reason;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal annualCost;
    private BigDecimal annualHours;
    private LocalDateTime updatedAt;

    public UUID teamId() {
        return teamId;
    }

    public void teamId(UUID teamId) {
        this.teamId = teamId;
    }

    public List<TeamProfileHistory> teamProfileHistories() {
        return teamProfileHistories;
    }

    public void teamProfileHistories(List<TeamProfileHistory> teamProfileHistories) {
        this.teamProfileHistories = teamProfileHistories;
    }

    public Reason reason() {
        return reason;
    }

    public void reason(Reason reason) {
        this.reason = reason;
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
        return "TeamHistory{" +
                "teamId=" + teamId +
                ", reason=" + reason +
                ", hourlyRate=" + hourlyRate +
                ", dayRate=" + dayRate +
                ", annualCost=" + annualCost +
                ", annualHours=" + annualHours +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
