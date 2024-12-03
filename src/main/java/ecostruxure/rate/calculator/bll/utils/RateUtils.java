package ecostruxure.rate.calculator.bll.utils;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.Project;
import ecostruxure.rate.calculator.be.ProjectTeam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class RateUtils {
    // Constants for financial and general scale precision and rounding mode
    private static final int FINANCIAL_SCALE = 4;
    private static final int GENERAL_SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = new BigDecimal("100.00");

    // Method to calculate the effective work hours based on effectiveness percentage
    public static BigDecimal effectiveWorkHours(Profile profile) {
        // If annual hours is zero, return zero to avoid division by zero
        if (profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(GENERAL_SCALE, ROUNDING_MODE);
        }
        BigDecimal effectiveness = profile.getEffectivenessPercentage().divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the effective work hours based on annual hours and effectiveness percentage
        BigDecimal effectiveWorkHours = profile.getAnnualHours().multiply(effectiveness).setScale(GENERAL_SCALE, ROUNDING_MODE);

        return effectiveWorkHours;
    }

    public static Project updateProjectRates(Project project) {
        var projectContainsMembers = project.getProjectTeams() != null;
        var projectContainsDayRate = project.getProjectDayRate() != null;
        var projectIsStarted = LocalDate.now().isAfter(project.getProjectStartDate());

        // Calculate, if rest cost have been calc. before
        if (projectContainsMembers && projectContainsDayRate && project.getProjectRestCostDate() == null) {
            project.setProjectTotalCostAtChange(calculateTotalCostAtChangeFirstTime(project));
            project.setProjectRestCostDate(LocalDate.now());
            project.setProjectDayRate(calculateDayRate(project.getProjectTeams()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }
        // Calculate, if project members are present and project is started
        else if (projectContainsMembers && projectContainsDayRate && projectIsStarted) {
            project.setProjectTotalCostAtChange(calculateTotalCostAtChange(project));
            project.setProjectRestCostDate(LocalDate.now());
            project.setProjectDayRate(calculateDayRate(project.getProjectTeams()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }
        // Calculate, if project members are present and project is not started
        else if (projectContainsMembers && projectContainsDayRate) {
            project.setProjectDayRate(calculateDayRate(project.getProjectTeams()));
            project.setProjectGrossMargin(calculateGrossMargin(project));
        }

        project.setProjectTotalDays(calculateWorkingDays(project.getProjectStartDate(), project.getProjectEndDate()));
        return project;
    }

    private static BigDecimal calculateTotalCostAtChangeFirstTime(Project project) {
        BigDecimal totalCostAtChange;
        var daysPassed = LocalDate.now().toEpochDay() - project.getProjectStartDate().toEpochDay();
        totalCostAtChange = project.getProjectDayRate().multiply(BigDecimal.valueOf(daysPassed));
        return totalCostAtChange;
    }

    private static BigDecimal calculateTotalCostAtChange(Project project) {
        BigDecimal totalCostAtChange = project.getProjectTotalCostAtChange();
        var daysPassed = LocalDate.now().toEpochDay() - project.getProjectRestCostDate().toEpochDay();
        totalCostAtChange = totalCostAtChange.add(project.getProjectDayRate().multiply(BigDecimal.valueOf(daysPassed)));
        return totalCostAtChange;
    }

    private static BigDecimal calculateDayRate(List<ProjectTeam> projectTeams) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        for (ProjectTeam projectTeam : projectTeams) {
            BigDecimal markup = projectTeam.getTeam().getMarkupPercentage();
            var dayRateWithMarkup = projectTeam.getTeam().getDayRate()
                    .multiply(BigDecimal.ONE.add(markup.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));
            var allocatedDayRate = dayRateWithMarkup.multiply(projectTeam.getAllocationPercentage()
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            totalDayRate = totalDayRate.add(allocatedDayRate);
        }
        return totalDayRate;
    }

    private static int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                count++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return count;
    }

    private static BigDecimal calculateGrossMargin(Project project) {
        BigDecimal grossMarginNumber = project.getProjectPrice().subtract(project.getProjectDayRate().multiply(BigDecimal.valueOf(project.getProjectTotalDays())));
        BigDecimal grossMarginPercentage = grossMarginNumber.divide(project.getProjectPrice(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return grossMarginPercentage;
    }
}/*

    // Method to calculate the hourly rate for a given profile
    public static BigDecimal hourlyRate(Profile profile) {
        // Ensure the profile is not null
        Objects.requireNonNull(profile, "Profile cannot be null");
        // If annual hours is zero, return zero to avoid division by zero
        if(profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // Calculate the hourly rate by dividing annual cost by annual hours
        return annualCost(profile).divide(profile.getAnnualHours(), GENERAL_SCALE, ROUNDING_MODE);
    }

    // Method to calculate the daily rate for a given profile
    public static BigDecimal dayRate(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        // Multiply hourly rate by the number of hours worked per day
        return hourlyRate(profile).multiply(profile.getHoursPerDay());
    }

    // Method to retrieve the annual cost of the profile
    public static BigDecimal annualCost(Profile profile) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        // Simply return the annual cost of the profile
        return profile.getAnnualCost();
    }

    // Method to calculate hourly rate adjusted by utilization percentage
    public static BigDecimal hourlyRate(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        // Convert percentage into decimal form
        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the adjusted hourly rate
        return hourlyRate(profile).multiply(percentageAsDecimal);
    }

    // Method to calculate daily rate adjusted by utilization percentage
    public static BigDecimal dayRate(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the adjusted daily rate
        return dayRate(profile).multiply(percentageAsDecimal);
    }

    // Method to calculate annual cost adjusted by utilization percentage
    public static BigDecimal annualCost(Profile profile, BigDecimal utilizationPercentage) {
        System.out.println("Entered annualCost in RateUtils");
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the adjusted annual cost
        return annualCost(profile).multiply(percentageAsDecimal);
    }

    // Method to calculate utilized hours based on utilization percentage
    public static BigDecimal utilizedHours(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the total utilized hours based on annual hours and utilization percentage
        return profile.getAnnualHours().multiply(percentageAsDecimal);
    }



    // Method to calculate the effectiveness percentage for the profile
    public static BigDecimal effectiveness(Profile profile) {
        // If annual hours is zero, return zero to avoid division by zero
        if (profile.getAnnualHours().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(GENERAL_SCALE, ROUNDING_MODE);
        }

        // Calculate the effectiveness as a percentage
        BigDecimal effectiveness = profile.getEffectivenessPercentage().divide(profile.getAnnualHours(), GENERAL_SCALE, ROUNDING_MODE);
        return effectiveness.multiply(HUNDRED);
    }

    // Method to calculate allocated hours based on allocation percentage
    public static BigDecimal allocatedHours(Profile profile, BigDecimal allocationPercentage){
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(allocationPercentage, "Allocation percentage cannot be null");

        BigDecimal hourAllocation = profile.getAnnualHours();
        BigDecimal percentageAsDecimal = allocationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the allocated hours
        return hourAllocation.multiply(percentageAsDecimal);
    }

    // Method to calculate utilized hours per day based on utilization percentage
    public static BigDecimal utilizedHoursPerDay(Profile profile, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the utilized hours per day based on hours per day and utilization percentage
        return profile.getHoursPerDay().multiply(percentageAsDecimal);
    }

    // Method to calculate the hourly rate for a team of profiles
    public static BigDecimal teamHourlyRate(List<Profile> profiles) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");

        // Calculate the total annual cost for all profiles
        BigDecimal totalAnnualCost = profiles.stream()
                .map(RateUtils::annualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate the total hours worked by all profiles
        BigDecimal totalHours = profiles.stream()
                .map(Profile::getAnnualHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // If total hours is zero, return zero to avoid division by zero
        if(totalHours.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate the total hourly rate for the team
        BigDecimal totalHourlyRate = totalAnnualCost.divide(totalHours, GENERAL_SCALE, ROUNDING_MODE);
        System.out.println("totalHourlyRate: " + totalHourlyRate);

        return totalHourlyRate;
    }

    // Method to calculate the daily rate for a team of profiles
    public static BigDecimal teamDayRate(List<Profile> profiles) {
        // Multiply the team's hourly rate by 8 (assuming 8-hour workdays)
        return teamHourlyRate(profiles).multiply(new BigDecimal("8.00"));
    }

    // Method to calculate the total annual cost for a team of profiles
    public static BigDecimal teamAnnualCost(List<Profile> profiles) {
        // Sum the annual costs of all profiles
        return profiles.stream()
                .map(RateUtils::annualCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Method to calculate the total utilized hours for a team of profiles based on utilization percentage
    public static BigDecimal teamUtilizedHours(List<Profile> profiles, BigDecimal utilizationPercentage) {
        Objects.requireNonNull(profiles, "Profiles cannot be null");
        Objects.requireNonNull(utilizationPercentage, "Utilization percentage cannot be null");

        // Calculate the total hours worked by all profiles
        BigDecimal totalHours = profiles.stream()
                .map(Profile::getAnnualHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Convert utilization percentage into decimal form
        BigDecimal percentageAsDecimal = utilizationPercentage.divide(HUNDRED, GENERAL_SCALE, ROUNDING_MODE);
        // Calculate the total utilized hours for the team
        return totalHours.multiply(percentageAsDecimal);
    }
}
*/

