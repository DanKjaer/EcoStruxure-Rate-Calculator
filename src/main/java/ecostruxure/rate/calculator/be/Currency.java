package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;

public class Currency {
    private String currencyCode;
    private BigDecimal eurConversionRate;
    private BigDecimal usdConversionRate;
    private String symbol;

    public Currency() {}

    public Currency(String currencyCode, BigDecimal eurConversionRate, BigDecimal usdConversionRate) {
        this.currencyCode = currencyCode;
        this.eurConversionRate = eurConversionRate;
        this.usdConversionRate = usdConversionRate;
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

    public BigDecimal usdConversionRate() {
        return usdConversionRate;
    }

    public void usdConversionRate(BigDecimal usdConversionRate) {
        this.usdConversionRate = usdConversionRate;
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

    public BigDecimal getUsdConversionRate() {
        return usdConversionRate;
    }

    public void setUsdConversionRate(BigDecimal usdConversionRate) {
        this.usdConversionRate = usdConversionRate;
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
                ", usdConversionRate=" + usdConversionRate +
                '}';
    }
}
