package ecostruxure.rate.calculator.gui.util;

import ecostruxure.rate.calculator.be.Profile;
import ecostruxure.rate.calculator.be.data.TeamMetrics;
import ecostruxure.rate.calculator.bll.utils.RateUtils;
import ecostruxure.rate.calculator.dal.db.ProfileDAO;
import ecostruxure.rate.calculator.dal.transaction.TransactionContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Calculations {

    private ProfileDAO profileDAO;

    private static final BigDecimal HOURS_PER_DAY = new BigDecimal("8.00");

    public Calculations() throws Exception{
        this.profileDAO = new ProfileDAO();
    }

    public TeamMetrics calculateMetrics(int teamId, List<Profile> profiles, TransactionContext context) throws Exception {
        // Burde nok ændres til instans variabler
        final int SCALE = 2;
        final RoundingMode roundingMode = RoundingMode.HALF_UP;

        BigDecimal totalAnnualCost = BigDecimal.ZERO;
        BigDecimal totalHours = BigDecimal.ZERO;

        for (Profile profile : profiles) {
            BigDecimal utilizationRate = profileDAO.getProfileRateUtilizationForTeam(context, profile.id(), teamId);
            BigDecimal utilizationHours = profileDAO.getProfileHourUtilizationForTeam(context, profile.id(), teamId);

            totalAnnualCost = totalAnnualCost.add(RateUtils.annualCost(profile, utilizationRate));
            totalHours = totalHours.add(RateUtils.utilizedHours(profile, utilizationHours));
        }

        BigDecimal hourlyRate = totalAnnualCost.divide(totalHours, SCALE, roundingMode);
        BigDecimal dayRate = hourlyRate.multiply(HOURS_PER_DAY);

        return new TeamMetrics(hourlyRate, dayRate, totalAnnualCost, totalHours);
    }
}
