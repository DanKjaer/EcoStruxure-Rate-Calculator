package ecostruxure.rate.calculator.be.data;

import java.math.BigDecimal;
import java.util.Objects;

public record ProfileMetrics(BigDecimal hourlyRate, BigDecimal dayRate, BigDecimal annualCost, BigDecimal totalHours, BigDecimal utilizationRate, BigDecimal utilizationHours) {
    public ProfileMetrics {
        Objects.requireNonNull(hourlyRate, "Hourly rate cannot be null");
        Objects.requireNonNull(dayRate, "Day rate cannot be null");
        Objects.requireNonNull(annualCost, "Annual cost cannot be null");
        Objects.requireNonNull(totalHours, "Total hours cannot be null");
        Objects.requireNonNull(utilizationRate, "Utilization rate cannot be null");
        Objects.requireNonNull(utilizationHours, "Utilization hours cannot be null");
    }
}
