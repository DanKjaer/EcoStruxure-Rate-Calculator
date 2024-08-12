package ecostruxure.rate.calculator.bll.utils;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.ProfileHistory;

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
        if(profile.effectiveWorkHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return annualCost(profile).divide(profile.effectiveWorkHours(), GENERAL_SCALE, ROUNDING_MODE);
    }

    public static BigDecimal dayRate(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        return hourlyRate(profile).multiply(profile.hoursPerDay());
    }

    public static BigDecimal annualCost(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");

        return profile.annualSalary().multiply(profile.overheadMultiplier()).add(profile.fixedAnnualAmount());
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

    public static BigDecimal utilizedHours(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return profile.effectiveWorkHours().multiply(percentageAsDecimal);
    }

    public static BigDecimal utilizedHoursPerDay(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return profile.hoursPerDay().multiply(percentageAsDecimal);
    }

    //This method causes the / by zero error.
    public static BigDecimal teamHourlyRate(List<Profile> profiles) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        BigDecimal totalAnnualCost = profiles.stream()
                .map(RateUtils::annualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEffectiveWorkHours = profiles.stream()
                .map(Profile::effectiveWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if(totalEffectiveWorkHours.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalHourlyRate = totalAnnualCost.divide(totalEffectiveWorkHours, GENERAL_SCALE, ROUNDING_MODE);
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

        BigDecimal totalEffectiveWorkHours = profiles.stream()
                .map(Profile::effectiveWorkHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        return totalEffectiveWorkHours.multiply(percentageAsDecimal);
    }

}
