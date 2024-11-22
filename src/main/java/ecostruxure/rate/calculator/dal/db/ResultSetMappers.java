package ecostruxure.rate.calculator.dal.db;

import ecostruxure.rate.calculator.be.Geography;
import ecostruxure.rate.calculator.be.Profile;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ResultSetMappers {
    public static Profile profileResultSet(ResultSet rs) throws SQLException {
        Geography geography = new Geography();
        geography.setId(rs.getInt("geography_id"));
        geography.setName(rs.getString("geography.name"));
        return new Profile.Builder()
                .setProfileId((UUID) rs.getObject("profile_id"))
                .setName(rs.getString("name"))
                .setCurrency(rs.getString("currency"))
                .setGeography(geography)
                .setResourceType(rs.getBoolean("resource_type"))
                .setAnnualCost(rs.getBigDecimal("annual_cost"))
                .setEffectivenessPercentage(rs.getBigDecimal("effectiveness"))
                .setAnnualHours(rs.getBigDecimal("annual_hours"))
                .setEffectiveWorkHours(rs.getBigDecimal("effective_work_hours"))
                .setHoursPerDay(rs.getBigDecimal("hours_per_day"))
                .setArchived(rs.getBoolean("is_archived"))
                .build();
    }
}