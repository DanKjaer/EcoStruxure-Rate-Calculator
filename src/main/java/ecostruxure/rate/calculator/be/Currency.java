package ecostruxure.rate.calculator.be;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Currency {

    @Id
    private String currencyCode;

    @Column(nullable = false)
    private BigDecimal eurConversionRate;

    @Column(nullable = false)
    private String symbol;

    public Currency() {}

    //region Getters and Setters
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getEurConversionRate() {
        return eurConversionRate;
    }

    public void setEurConversionRate(BigDecimal eurConversionRate) {
        this.eurConversionRate = eurConversionRate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    //endregion
}
