package ecostruxure.rate.calculator.bll.utils;

import ecostruxure.rate.calculator.be.*;

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

    /**
     * This method is for calculations on profiles
     *  Method to calculate the effective work hours based on effectiveness percentage
     * @param profile
     * @return
     */
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

    /**
     * These methods are for calculations on teams
     */
    public static Team calculateTotalAllocatedHoursAndCost(Team team){
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalHours = BigDecimal.ZERO;
        for (TeamProfile teamProfile : team.getTeamProfiles()) {
            totalCost = totalCost.add(teamProfile.getAllocatedCost());
            totalHours = totalHours.add(teamProfile.getAllocatedHours());
        }
        team.setTotalAllocatedHours(totalHours);
        team.setTotalAllocatedCost(totalCost);
        return team;
    }

    public static Team calculateRates(Team team) {
        team.setHourlyRate(team.getTotalAllocatedCost()
                .divide(team.getTotalAllocatedHours(), 2, BigDecimal.ROUND_HALF_UP));
        team.setDayRate(team.getHourlyRate().multiply(BigDecimal.valueOf(8)));
        return team;
    }

    public static Team calculateTotalMarkupAndTotalGrossMargin(Team team) {
        BigDecimal markup = team.getMarkupPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal grossMargin = team.getGrossMarginPercentage().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        BigDecimal totalAnnualCost = team.getTotalAllocatedCost();

        BigDecimal totalMarkup = totalAnnualCost.multiply(markup);
        BigDecimal totalGrossMargin = totalMarkup.multiply(grossMargin);

        team.setTotalCostWithMarkup(totalMarkup);
        team.setTotalCostWithGrossMargin(totalGrossMargin);

        return team;
    }

    /**
     * These methods are for calculations on projects
     * @param project
     * @return
     */
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
}