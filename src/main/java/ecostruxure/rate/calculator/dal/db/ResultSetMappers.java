package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Profile;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ResultSetMappers {
    public static Profile profileResultSet(ResultSet rs) throws SQLException {
        return new Profile.Builder()
                .setProfileId((UUID) rs.getObject("profile_id"))
                .setName(rs.getString("name"))
                .setCurrency(rs.getString("currency"))
                .setCountryId(rs.getInt("country_id"))
                .setResourceType(rs.getBoolean("resource_type"))
                .setAnnualCost(rs.getBigDecimal("annual_cost"))
                .setEffectivenessPercentage(rs.getBigDecimal("effectiveness"))
                .setAnnualHours(rs.getBigDecimal("annual_hours"))
                .setEffectiveWorkHours(rs.getBigDecimal("effective_work_hours"))
                .setHoursPerDay(rs.getBigDecimal("hours_per_day"))
                .setArchived(rs.getBoolean("is_archived"))
                .setCostAllocation(rs.getBigDecimal("cost_allocation"))
                .setHourAllocation(rs.getBigDecimal("hour_allocation"))
                .build();
    }
}