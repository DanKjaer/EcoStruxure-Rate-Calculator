package ecostruxure.rate.calculator.be.data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record TeamMetrics(BigDecimal hourlyRate, BigDecimal dayRate, BigDecimal annualCost, BigDecimal totalHours) {
    public TeamMetrics {
        Objects.requireNonNull(hourlyRate, "Hourly rate cannot be null");
        Objects.requireNonNull(dayRate, "Day rate cannot be null");
        Objects.requireNonNull(annualCost, "Annual cost cannot be null");
        Objects.requireNonNull(totalHours, "Total hours cannot be null");
    }
}
