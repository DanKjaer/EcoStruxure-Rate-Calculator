package ecostruxure.rate.calculator.be;
import java.math.BigDecimal;
import java.util.Objects;

public class Profile {
    private int id;
    private BigDecimal annualSalary;
    private BigDecimal effectiveness;
    private BigDecimal effectiveWorkHours;

    private BigDecimal utilizationRate;
    private BigDecimal utilizationHours;

    private BigDecimal hoursPerDay;
    private ProfileData profileData;

    public Profile() {

    }

    public Profile(int id, String name, String currency, BigDecimal annualSalary,
                   BigDecimal effectiveness, int geography,
                   BigDecimal effectiveWorkHours, boolean overhead,
                   BigDecimal hoursPerDay, boolean archived) {
        this.id = id;
        this.annualSalary = annualSalary;
        this.effectiveness = effectiveness;
        this.effectiveWorkHours = effectiveWorkHours;
        this.hoursPerDay = hoursPerDay;

        this.profileData = new ProfileData(id, name, currency, geography, overhead, archived);
    }

    public Profile(int id, String name, String currency, BigDecimal annualSalary,
                   BigDecimal effectiveness, int geography,
                   BigDecimal effectiveWorkHours, BigDecimal utilizationRate, BigDecimal utilizationHours, boolean overhead,
                   BigDecimal hoursPerDay, boolean archived) {
        this.id = id;
        this.annualSalary = annualSalary;
        this.effectiveness = effectiveness;
        this.effectiveWorkHours = effectiveWorkHours;
        this.utilizationRate = setUtilization(utilizationRate);
        this.utilizationHours = setUtilization(utilizationHours);
        this.hoursPerDay = hoursPerDay;

        this.profileData = new ProfileData(id, name, currency, geography, overhead, archived);
    }

    public Profile(String name, String currency, BigDecimal annualSalary,
                   BigDecimal effectiveness, int geography,
                   BigDecimal effectiveWorkHours, BigDecimal utilizationRate, BigDecimal utilizationHours, boolean overhead,
                   BigDecimal hoursPerDay, boolean archived) {
        this.annualSalary = annualSalary;
        this.effectiveness = effectiveness;
        this.effectiveWorkHours = effectiveWorkHours;
        this.utilizationRate = setUtilization(utilizationRate);
        this.utilizationHours = setUtilization(utilizationHours);
        this.hoursPerDay = hoursPerDay;

        this.profileData = new ProfileData(name, currency, geography, overhead, archived);
    }

    private BigDecimal setUtilization(BigDecimal utilization) {
        return Objects.requireNonNullElse(utilization, BigDecimal.ZERO);
    }

    public int id() {
        return this.id;
    }

    public void id(int id) {
        this.id = id;
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

    public void hoursPerDay(BigDecimal hoursPerDay) {
        this.hoursPerDay = hoursPerDay;
    }

    public ProfileData profileData() {
        return profileData;
    }

    public void profileData(ProfileData profileData) {
        this.profileData = profileData;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", annualSalary=" + annualSalary +
                ", effectiveness=" + effectiveness +
                ", effectiveWorkHours=" + effectiveWorkHours +
                ", utilizationRate=" + utilizationRate +
                ", utilizationHours=" + utilizationHours +
                ", hoursPerDay=" + hoursPerDay +
                ", profileData=" + profileData +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Profile that = (Profile) obj;
        return id == that.id &&
                Objects.equals(annualSalary, that.annualSalary) &&
                Objects.equals(effectiveness, that.effectiveness) &&
                Objects.equals(effectiveWorkHours, that.effectiveWorkHours) &&
                Objects.equals(hoursPerDay, that.hoursPerDay) &&
                Objects.equals(profileData.overhead(), that.profileData.overhead());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annualSalary, effectiveness, effectiveWorkHours, hoursPerDay, profileData.overhead());
    }

    public static void main(String[] args) {
        var p = new Profile();
        p.annualSalary(new BigDecimal("50000.00"));
        p.effectiveWorkHours(new BigDecimal("6000"));
        p.effectiveness(new BigDecimal("0.8"));
        p.utilizationRate(new BigDecimal("80"));
        /**
         * 500000.00
         * 600
         * 0.8
         * 80
         */
        System.out.println(p.annualSalary());
    }
}
