package ecostruxure.rate.calculator.be.data;

import java.math.BigDecimal;

public record Rates(BigDecimal rawRate, BigDecimal markupRate, BigDecimal grossMarginRate) {
}