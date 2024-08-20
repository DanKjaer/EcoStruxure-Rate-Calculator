package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class Team{
    private UUID teamId;
    private String name;
    private BigDecimal markup;
    private BigDecimal grossMargin;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal totalAllocatedHours;
    private BigDecimal totalAllocatedCost;
    private Timestamp updatedAt;
    private boolean archived;

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

    public BigDecimal getMarkup() {
        return markup;
    }

    public void setMarkup(BigDecimal markup) {
        this.markup = markup;
    }

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}

//public class Team {
//    private int id;
//    private String name;
//    private BigDecimal markup;
//    private BigDecimal grossMargin;
//    private boolean archived;
//
//    public Team() {}
//
//    public Team(int id) {
//        this.id = id;
//    }
//
//    public Team(int id, String name, BigDecimal markup, BigDecimal grossMargin, boolean archived) {
//        this.id = id;
//        this.name = name;
//        this.markup = markup;
//        this.grossMargin = grossMargin;
//        this.archived = archived;
//    }
//
//    public Team(String name, BigDecimal markup, BigDecimal grossMargin) {
//        this(0, name, markup, grossMargin, false);
//    }
//
//    public void id(int id) {
//        this.id = id;
//    }
//
//    public int id() {
//        return this.id;
//    }
//
//    public void name(String name) {
//        this.name = name;
//    }
//
//    public String name() {
//        return name;
//    }
//
//    public void markup(BigDecimal markup) {
//        this.markup = markup;
//    }
//
//    public BigDecimal markup() {
//        return markup;
//    }
//
//    public BigDecimal grossMargin() {
//        return grossMargin;
//    }
//
//    public void grossMargin(BigDecimal grossMargin) {
//        this.grossMargin = grossMargin;
//    }
//
//    public boolean archived() {
//        return archived;
//    }
//
//    public void archived(boolean archived) {
//        this.archived = archived;
//    }
//
//    @Override
//    public String toString() {
//        return "Team{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if ((!(obj instanceof Team team))) return false;
//        return id == team.id;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
//}
