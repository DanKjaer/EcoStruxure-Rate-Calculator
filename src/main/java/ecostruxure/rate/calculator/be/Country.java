package ecostruxure.rate.calculator.be;

import java.math.BigDecimal;
import java.util.Objects;

public class Country {
    private final String code;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public Country(String code, String name, BigDecimal latitude, BigDecimal longitude) {
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Country(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public BigDecimal latitude() {
        return latitude;
    }

    public BigDecimal longitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Country country)) return false;
        return this.code.equals(country.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
