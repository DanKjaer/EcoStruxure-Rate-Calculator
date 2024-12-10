package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;

public class DashboardProjectDTO {
    private String name;
    private BigDecimal cost;
    private BigDecimal totalCost;

    public DashboardProjectDTO(String name, BigDecimal cost, BigDecimal totalCost) {
        this.name = name;
        this.cost = cost;
        this.totalCost = totalCost;
    }

    //#region Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    //#endregion
}
