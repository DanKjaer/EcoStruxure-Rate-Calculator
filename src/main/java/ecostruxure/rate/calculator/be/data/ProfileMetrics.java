package ecostruxure.rate.calculator.be.data;

import java.math.BigDecimal;
import java.util.Objects;

public record ProfileMetrics(BigDecimal hourlyRate, BigDecimal dayRate, BigDecimal annualCost, BigDecimal totalHours, BigDecimal costAllocation, BigDecimal hourAllocation) {
    public ProfileMetrics {
        Objects.requireNonNull(hourlyRate, "Hourly rate cannot be null");
        Objects.requireNonNull(dayRate, "Day rate cannot be null");
        Objects.requireNonNull(annualCost, "Annual cost cannot be null");
        Objects.requireNonNull(totalHours, "Total hours cannot be null");
        Objects.requireNonNull(costAllocation, "Allocated cost cannot be null");
        Objects.requireNonNull(hourAllocation, "Allocated hours cannot be null");
    }
}
