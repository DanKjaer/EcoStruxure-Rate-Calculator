package ecostruxure.rate.calculator.bll.utils;

import ecostruxure.rate.calculator.be.Profile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class RateUtils {
    private static final int FINANCIAL_SCALE = 4;
    private static final int GENERAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100.00");

    // Basic rate calculation methods
    public static BigDecimal hourlyRate(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        if(profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return annualCost(profile).divide(profile.getAnnualHours(), GENERAL_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal dayRate(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        return hourlyRate(profile).multiply(profile.getHoursPerDay());
    }

    public static BigDecimal annualCost(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        return profile.getAnnualCost();
    }

    //Basic rate calculations w/ utilization
    public static BigDecimal hourlyRate(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);

        return hourlyRate(profile).multiply(percentageAsDecimal);
    }


    public static BigDecimal dayRate(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return dayRate(profile).multiply(percentageAsDecimal);
    }

    public static BigDecimal annualCost(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return annualCost(profile).multiply(percentageAsDecimal);
    }

    // Kan bruges til at udregne effectiveness for en profil
    public static BigDecimal utilizedHours(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return profile.getAnnualHours().multiply(percentageAsDecimal);
    }

    public static BigDecimal effectiveWorkHours(Profile profile) {
        if (profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(GENERAL_SCALE, ROUNDING_MODE);
        }

        BigDecimal effectiveness = profile.getEffectivenessPercentage().divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        BigDecimal effectiveWorkHours = profile.getAnnualHours().multiply(effectiveness).setScale(GENERAL_SCALE, ROUNDING_MODE);

        return effectiveWorkHours;
    }

    public static BigDecimal effectiveness(Profile profile) {
        if (profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(GENERAL_SCALE, ROUNDING_MODE);
        }

        BigDecimal effectiveness = profile.getEffectivenessPercentage().divide(profile.getAnnualHours(), GENERAL_SCALE, ROUNDING_MODE);
        return effectiveness.multiply(HUNDRED);
    }

    public static BigDecimal allocatedHours(Profile profile, BigDecimal allocationPercentage){
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(allocationPercentage, "Allocation percentage cannot be null");

        BigDecimal hourAllocation = profile.getAnnualHours();
        BigDecimal percentageAsDecimal = allocationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return hourAllocation.multiply(percentageAsDecimal);
    }

    public static BigDecimal utilizedHoursPerDay(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return profile.getHoursPerDay().multiply(percentageAsDecimal);
    }

    public static BigDecimal teamHourlyRate(List<Profile> profiles) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        BigDecimal totalAnnualCost = profiles.stream()
                .map(RateUtils::annualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHours = profiles.stream()
                .map(Profile::getAnnualHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if(totalHours.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalHourlyRate = totalAnnualCost.divide(totalHours, GENERAL_SCALE, ROUNDING_MODE);
        System.out.println("totalHourlyRate: " + totalHourlyRate);

        return totalHourlyRate;
    }

    public static BigDecimal teamDayRate(List<Profile> profiles) {
        return teamHourlyRate(profiles).multiply(new BigDecimal("8.00"));
    }

    public static BigDecimal teamAnnualCost(List<Profile> profiles) {
        return profiles.stream()
                .map(RateUtils::annualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal teamUtilizedHours(List<Profile> profiles, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal totalHours = profiles.stream()
                .map(Profile::getAnnualHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return totalHours.multiply(percentageAsDecimal);
    }

}
