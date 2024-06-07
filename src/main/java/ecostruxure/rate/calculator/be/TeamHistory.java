package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamHistory {
    public enum Reason {
        TEAM_CREATED,
        ASSIGNED_PROFILE,
        REMOVED_PROFILE,
        UPDATED_PROFILE,
        UTILIZATION_CHANGE,
    }

    private int teamId;
    private List<TeamProfileHistory> teamProfileHistories = new ArrayList<>();
    private Reason reason;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal annualCost;
    private BigDecimal totalHours;
    private LocalDateTime updatedAt;

    public int teamId() {
        return teamId;
    }

    public void teamId(int teamId) {
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
        return "TeamHistory{" +
                "teamId=" + teamId +
                ", reason=" + reason +
                ", hourlyRate=" + hourlyRate +
                ", dayRate=" + dayRate +
                ", annualCost=" + annualCost +
                ", totalHours=" + totalHours +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
