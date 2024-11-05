package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class Team {
    private UUID teamId;
    private String name;
    private BigDecimal markup;
    private BigDecimal totalMarkup;
    private BigDecimal grossMargin;
    private BigDecimal totalGrossMargin;
    private BigDecimal hourlyRate;
    private BigDecimal dayRate;
    private BigDecimal totalAllocatedHours;
    private BigDecimal totalAllocatedCost;
    private Timestamp updatedAt;
    private boolean archived;

    public Team() {}

    private Team(Builder builder) {
        this.teamId = builder.teamId;
        this.name = builder.name;
        this.markup = builder.markup;
        this.totalMarkup = builder.totalMarkup;
        this.grossMargin = builder.grossMargin;
        this.totalGrossMargin = builder.totalGrossMargin;
        this.hourlyRate = builder.hourlyRate;
        this.dayRate = builder.dayRate;
        this.totalAllocatedHours = builder.totalAllocatedHours;
        this.totalAllocatedCost = builder.totalAllocatedCost;
        this.updatedAt = builder.updatedAt;
        this.archived = builder.archived;
    }

    public static class Builder {
        private UUID teamId;
        private String name;
        private BigDecimal markup;
        private BigDecimal totalMarkup;
        private BigDecimal grossMargin;
        private BigDecimal totalGrossMargin;
        private BigDecimal hourlyRate;
        private BigDecimal dayRate;
        private BigDecimal totalAllocatedHours;
        private BigDecimal totalAllocatedCost;
        private Timestamp updatedAt;
        private boolean archived;


        public Builder teamId(UUID teamId) {
            this.teamId = teamId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder markup(BigDecimal markup) {
            this.markup = markup;
            return this;
        }

        public Builder totalMarkup(BigDecimal totalMarkup) {
            this.totalMarkup = totalMarkup;
            return this;
        }

        public Builder grossMargin(BigDecimal grossMargin) {
            this.grossMargin = grossMargin;
            return this;
        }

        public Builder totalGrossMargin(BigDecimal totalGrossMargin) {
            this.totalGrossMargin = totalGrossMargin;
            return this;
        }

        public Builder hourlyRate(BigDecimal hourlyRate) {
            this.hourlyRate = hourlyRate;
            return this;
        }

        public Builder dayRate(BigDecimal dayRate) {
            this.dayRate = dayRate;
            return this;
        }

        public Builder totalAllocatedHours(BigDecimal totalAllocatedHours) {
            this.totalAllocatedHours = totalAllocatedHours;
            return this;
        }

        public Builder totalAllocatedCost(BigDecimal totalAllocatedCost) {
            this.totalAllocatedCost = totalAllocatedCost;
            return this;
        }

        public Builder updatedAt(Timestamp updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder archived(boolean archived) {
            this.archived = archived;
            return this;
        }

        public Team build() {
            return new Team(this);
        }
    }

    // Getters og setters
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

    public BigDecimal getTotalMarkup() {
        return totalMarkup;
    }

    public void setTotalMarkup(BigDecimal totalMarkup) {
        this.totalMarkup = totalMarkup;
    }

    public BigDecimal getTotalGrossMargin() {
        return totalGrossMargin;
    }

    public void setTotalGrossMargin(BigDecimal totalGrossMargin) {
        this.totalGrossMargin = totalGrossMargin;
    }
}