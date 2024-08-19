package ecostruxure.rate.calculator.be;
import javafx.beans.property.BooleanProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class Profile {
    private UUID profileId;
    private String name;
    private String currency;
    private int countryId;
    private Boolean resourceType;
    private BigDecimal annualCost;
    private BigDecimal annualHours;
    private BigDecimal hoursPerDay;
    private BigDecimal effectivenessPercentage;
    private BigDecimal effectiveWorkHours;
    private boolean archived;
    private Timestamp updatedAt;
    private BigDecimal costAllocation;
    private BigDecimal hourAllocation;

    // Implementer builder pattern, så jeg kan have 1 constructor.

    private Profile(Builder builder){
        this.profileId = builder.profileId;
        this.name = builder.name;
        this.currency = builder.currency;
        this.countryId = builder.countryId;
        this.resourceType = builder.resourceType;
        this.annualCost = builder.annualCost;
        this.annualHours = builder.annualHours;
        this.hoursPerDay = builder.hoursPerDay;
        this.effectivenessPercentage = builder.effectivenessPercentage;
        this.effectiveWorkHours = builder.effectiveWorkHours;
        this.archived = builder.archived;
        this.updatedAt = builder.updatedAt;
        this.costAllocation = builder.costAllocation;
        this.hourAllocation = builder.hourAllocation;
    }

    public static class Builder{
        private UUID profileId;
        private String name;
        private String currency;
        private int countryId;
        private Boolean resourceType;
        private BigDecimal annualCost;
        private BigDecimal annualHours;
        private BigDecimal hoursPerDay;
        private BigDecimal effectivenessPercentage;
        private BigDecimal effectiveWorkHours;
        private boolean archived;
        private Timestamp updatedAt;
        private BigDecimal costAllocation;
        private BigDecimal hourAllocation;

        public Builder setProfileId(UUID profileId){
            this.profileId = profileId;
            return this;
        }

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder setCurrency(String currency){
            this.currency = currency;
            return this;
        }

        public Builder setCountryId(int countryId){
            this.countryId = countryId;
            return this;
        }

        public Builder setResourceType(Boolean resourceType){
            this.resourceType = resourceType;
            return this;
        }

        public Builder setAnnualCost(BigDecimal annualCost){
            this.annualCost = annualCost;
            return this;
        }

        public Builder setAnnualHours(BigDecimal annualHours){
            this.annualHours = annualHours;
            return this;
        }

        public Builder setHoursPerDay(BigDecimal hoursPerDay){
            this.hoursPerDay = hoursPerDay;
            return this;
        }

        public Builder setEffectivenessPercentage(BigDecimal effectivenessPercentage){
            this.effectivenessPercentage = effectivenessPercentage;
            return this;
        }

        public Builder setEffectiveWorkHours(BigDecimal effectiveWorkHours){
            this.effectiveWorkHours = effectiveWorkHours;
            return this;
        }

        public Builder setArchived(boolean archived){
            this.archived = archived;
            return this;
        }

        public Builder setUpdatedAt(Timestamp updatedAt){
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder setCostAllocation(BigDecimal costAllocation){
            this.costAllocation = costAllocation;
            return this;
        }

        public Builder setHourAllocation(BigDecimal hourAllocation){
            this.hourAllocation = hourAllocation;
            return this;
        }

        public Profile build(){
            return new Profile(this);
        }

    }
    public Profile() {
    }

    public UUID getProfileId() {
        return profileId;
    }

    public BigDecimal getHoursPerDay() {
        return hoursPerDay;
    }

    public void setHoursPerDay(BigDecimal hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public Boolean isResourceType() {
        return resourceType;
    }

    public void setResourceType(Boolean resourceType) {
        this.resourceType = resourceType;
    }

    public BigDecimal getAnnualCost() {
        return annualCost;
    }

    public void setAnnualCost(BigDecimal annualCost) {
        this.annualCost = annualCost;
    }

    public BigDecimal getAnnualHours() {
        return annualHours;
    }

    public void setAnnualHours(BigDecimal annualHours) {
        this.annualHours = annualHours;
    }

    public BigDecimal getEffectivenessPercentage() {
        return effectivenessPercentage;
    }

    public void setEffectivenessPercentage(BigDecimal effectivenessPercentage) {
        this.effectivenessPercentage = effectivenessPercentage;
    }

    public BigDecimal getEffectiveWorkHours() {
        return effectiveWorkHours;
    }

    public void setEffectiveWorkHours(BigDecimal effectiveWorkHours) {
        this.effectiveWorkHours = effectiveWorkHours;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getCostAllocation() {
        return costAllocation;
    }

    public void setCostAllocation(BigDecimal costAllocation) {
        this.costAllocation = costAllocation;
    }

    public BigDecimal getHourAllocation() {
        return hourAllocation;
    }

    public void setHourAllocation(BigDecimal hourAllocation) {
        this.hourAllocation = hourAllocation;
    }
}