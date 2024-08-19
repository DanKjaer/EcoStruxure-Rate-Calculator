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

    public Profile() {
    }

    public Profile(String name, Boolean resourceType) {
        this.name = name;
        this.resourceType = resourceType;
    }

    public Profile(UUID profileId, String name, String currency, int countryId, boolean resourceType, BigDecimal annualCost, BigDecimal annualHours, BigDecimal hoursPerDay, BigDecimal effectivenessPercentage, BigDecimal effectiveWorkHours, boolean archived, Timestamp updatedAt) {
        this.profileId = profileId;
        this.name = name;
        this.currency = currency;
        this.countryId = countryId;
        this.resourceType = resourceType;
        this.annualCost = annualCost;
        this.annualHours = annualHours;
        this.hoursPerDay = hoursPerDay;
        this.effectivenessPercentage = effectivenessPercentage;
        this.effectiveWorkHours = effectiveWorkHours;
        this.archived = archived;
        this.updatedAt = updatedAt;
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

//public class Profile {
//    private int id;
//    private BigDecimal annualSalary;
//    private BigDecimal effectiveness;
//    private BigDecimal totalHours;
//    private BigDecimal effectiveWorkHours;
//

//
//    private BigDecimal hoursPerDay;
//    private ProfileData profileData;
//
//    public Profile() {
//
//    }
//    // Constructor without allocation
//    public Profile(int id, String name, String currency, BigDecimal annualSalary,
//                   BigDecimal effectiveness, int geography,
//                   BigDecimal totalHours, BigDecimal effectiveWorkHours, boolean overhead,
//                   BigDecimal hoursPerDay, boolean archived) {
//        this.id = id;
//        this.annualSalary = annualSalary;
//        this.effectiveness = effectiveness;
//        this.totalHours = totalHours;
//        this.effectiveWorkHours = effectiveWorkHours;
//        this.hoursPerDay = hoursPerDay;
//
//        this.profileData = new ProfileData(id, name, currency, geography, overhead, archived);
//    }
//    // Constructor with id
//    public Profile(int id, String name, String currency, BigDecimal annualSalary,
//                   BigDecimal effectiveness, int geography,
//                   BigDecimal totalHours, BigDecimal effectiveWorkHours, BigDecimal costAllocation, BigDecimal hourAllocation, boolean overhead,
//                   BigDecimal hoursPerDay, boolean archived) {
//        this.id = id;
//        this.annualSalary = annualSalary;
//        this.effectiveness = effectiveness;
//        this.totalHours = totalHours;
//        this.effectiveWorkHours = effectiveWorkHours;
//        this.costAllocation = setUtilization(costAllocation);
//        this.hourAllocation = setUtilization(hourAllocation);
//        this.hoursPerDay = hoursPerDay;
//
//        this.profileData = new ProfileData(id, name, currency, geography, overhead, archived);
//        //System.out.println("Profile constructor: hourAllocation =" + this.hourAllocation);
//    }
//
//    // Constructor without id
//    public Profile(String name, String currency, BigDecimal annualSalary,
//                   BigDecimal effectiveness, int geography,
//                   BigDecimal totalHours, BigDecimal effectiveWorkHours, BigDecimal costAllocation, BigDecimal hourAllocation, boolean overhead,
//                   BigDecimal hoursPerDay, boolean archived) {
//        this.annualSalary = annualSalary;
//        this.effectiveness = effectiveness;
//        this.totalHours = totalHours;
//        this.effectiveWorkHours = effectiveWorkHours;
//        this.costAllocation = setUtilization(costAllocation);
//        this.hourAllocation = setUtilization(hourAllocation);
//        this.hoursPerDay = hoursPerDay;
//
//        this.profileData = new ProfileData(name, currency, geography, overhead, archived);
//    }
//
//    private BigDecimal setUtilization(BigDecimal utilization) {
//        return Objects.requireNonNullElse(utilization, BigDecimal.ZERO);
//    }
//
//    public int id() {
//        return this.id;
//    }
//
//    public void id(int id) {
//        this.id = id;
//    }
//
//    public BigDecimal annualSalary() {
//        return annualSalary;
//    }
//
//    public void annualSalary(BigDecimal annualSalary) {
//        this.annualSalary = annualSalary;
//    }
//
//    public BigDecimal effectiveness() {
//        return effectiveness;
//    }
//
//    public void effectiveness(BigDecimal effectiveness) {
//        this.effectiveness = effectiveness;
//    }
//
//    public BigDecimal totalHours() {
//        return totalHours;
//    }
//
//    public void totalHours(BigDecimal totalHours) {
//        this.totalHours = totalHours;
//    }
//
//    public void effectiveWorkHours(BigDecimal effectiveWorkHours) {
//        this.effectiveWorkHours = effectiveWorkHours;
//    }
//
//    public BigDecimal effectiveWorkHours() {
//        return effectiveWorkHours;
//    }
//
//    public BigDecimal hoursPerDay() {
//        return hoursPerDay;
//    }
//

//
//    public void hoursPerDay(BigDecimal hoursPerDay) {
//        this.hoursPerDay = hoursPerDay;
//    }
//
//    public ProfileData profileData() {
//        return profileData;
//    }
//
//    public void profileData(ProfileData profileData) {
//        this.profileData = profileData;
//    }
//
//    @Override
//    public String toString() {
//        return "Profile{" +
//                "id=" + id +
//                ", annualSalary=" + annualSalary +
//                ", effectiveness=" + effectiveness +
//                ", totalHours=" + totalHours +
//                ", effectiveWorkHours=" + effectiveWorkHours +
//                ", costAllocation=" + costAllocation +
//                ", hourAllocation=" + hourAllocation +
//                ", hoursPerDay=" + hoursPerDay +
//                ", profileData=" + profileData +
//                '}';
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//        Profile that = (Profile) obj;
//        return id == that.id &&
//                Objects.equals(annualSalary, that.annualSalary) &&
//                Objects.equals(effectiveness, that.effectiveness) &&
//                Objects.equals(totalHours, that.totalHours) &&
//                Objects.equals(effectiveWorkHours, that.effectiveWorkHours) &&
//                Objects.equals(hoursPerDay, that.hoursPerDay) &&
//                Objects.equals(profileData.overhead(), that.profileData.overhead());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, annualSalary, effectiveness, totalHours, effectiveWorkHours, hoursPerDay, profileData.overhead());
//    }
//
//    public static void main(String[] args) {
//        var p = new Profile();
//        p.annualSalary(new BigDecimal("50000.00"));
//        p.totalHours(new BigDecimal("6000"));
//        p.effectiveWorkHours(new BigDecimal("4800"));
//        p.effectiveness(new BigDecimal("80"));
//        p.costAllocation(new BigDecimal("100"));
//        p.hourAllocation(new BigDecimal("100"));
//        /**
//         * 500000.00
//         * 6000
//         * 4800
//         * 80
//         * 100
//         * 100
//         */
//        System.out.println(p.annualSalary());
//    }
//}
