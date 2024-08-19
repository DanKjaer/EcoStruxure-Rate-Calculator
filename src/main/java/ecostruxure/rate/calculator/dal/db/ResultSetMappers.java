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
                .setCountryId(rs.getInt("geography"))
                .setResourceType(rs.getBoolean("overhead"))
                .setAnnualCost(rs.getBigDecimal("annual_cost"))
                .setEffectivenessPercentage(rs.getBigDecimal("effectiveness"))
                .setAnnualHours(rs.getBigDecimal("total_hours"))
                .setEffectiveWorkHours(rs.getBigDecimal("effective_work_hours"))
                .setHoursPerDay(rs.getBigDecimal("hours_per_day"))
                .setArchived(rs.getBoolean("archived"))
                .setCostAllocation(rs.getBigDecimal("cost_allocation"))
                .setHourAllocation(rs.getBigDecimal("hour_allocation"))
                .build();
    }
}
//        // ProfileData
//        UUID id = (UUID) rs.getObject("id");
//        String name = rs.getString("name");
//        String currency = rs.getString("currency");
//        int countryId = rs.getInt("geography");
//        boolean resourceType = rs.getBoolean("overhead");
//
//        // Profile
//        BigDecimal annualCost = rs.getBigDecimal("annual_cost");
//        BigDecimal effectivenessPercentage = rs.getBigDecimal("effectiveness");
//        BigDecimal annualHours = rs.getBigDecimal("total_hours");
//        BigDecimal effectiveWorkHours = rs.getBigDecimal("effective_work_hours");
//        BigDecimal hoursPerDay = rs.getBigDecimal("hours_per_day");
//        boolean archived = rs.getBoolean("archived");
//
//        return new Profile(
//                id,
//                name,
//                currency,
//                countryId,
//                resourceType,
//                annualCost,
//                effectivenessPercentage,
//                annualHours,
//                effectiveWorkHours,
//                hoursPerDay,
//                archived
//        );
//    }
//}
