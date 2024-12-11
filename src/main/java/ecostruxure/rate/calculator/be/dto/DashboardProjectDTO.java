package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;

public class DashboardProjectDTO {
    private String name;
    private BigDecimal price;
    private BigDecimal grossMargin;

    public DashboardProjectDTO(String name, BigDecimal price, BigDecimal grossMargin) {
        this.name = name;
        this.price = price;
        this.grossMargin = grossMargin;
    }

    //#region Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
    }
    //#endregion
}
