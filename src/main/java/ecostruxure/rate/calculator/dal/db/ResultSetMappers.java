package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Profile;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetMappers {
    public static Profile profileResultSet(ResultSet rs) throws SQLException {
        // ProfileData
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String currency = rs.getString("currency");
        int geography = rs.getInt("geography");
        boolean overhead = rs.getBoolean("overhead");
        boolean archived = rs.getBoolean("archived");

        // Profile
        BigDecimal annualSalary = rs.getBigDecimal("annual_salary");
        BigDecimal fixedAnnualAmount = rs.getBigDecimal("fixed_annual_amount");
        BigDecimal overheadMultiplier = rs.getBigDecimal("overhead_multiplier");
        BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
        BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");

        return new Profile(
                id,
                name,
                currency,
                annualSalary,
                fixedAnnualAmount,
                overheadMultiplier,
                geography,
                effectiveWorkHours,
                overhead,
                hoursPerDay,
                archived
        );
    }
}
