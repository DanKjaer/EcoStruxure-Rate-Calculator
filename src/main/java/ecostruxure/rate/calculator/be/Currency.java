package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;

public class Currency {
    private String currencyCode;
    private BigDecimal eurConversionRate;
    private String symbol;

    public Currency() {}

    public Currency(String currencyCode, BigDecimal eurConversionRate, String symbol) {
        this.currencyCode = currencyCode;
        this.eurConversionRate = eurConversionRate;
        this.symbol = symbol;
    }

    public String currencyCode() {
        return currencyCode;
    }

    public void currencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal eurConversionRate() {
        return eurConversionRate;
    }

    public void eurConversionRate(BigDecimal eurConversionRate) {
        this.eurConversionRate = eurConversionRate;
    }

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

    @Override
    public String toString() {
        return "Currency{" +
                "currencyCode='" + currencyCode + '\'' +
                ", eurConversionRate=" + eurConversionRate +
                '}';
    }
}
