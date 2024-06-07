package ecostruxure.rate.calculator.be.enums;

public enum CurrencyEnum {
    USD("$"),
    EUR("€");

    private final String symbol;

    CurrencyEnum(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }
}
