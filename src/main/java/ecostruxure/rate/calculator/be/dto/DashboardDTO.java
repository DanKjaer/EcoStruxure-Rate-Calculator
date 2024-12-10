package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;

public class DashboardDTO {
    private String name;
    private BigDecimal dayRate;
    private BigDecimal totalGrossMargin;
    private BigDecimal totalPrice;
    private BigDecimal totalCost;
    private DashboardProjectDTO[] projects;

    public DashboardDTO(String name, BigDecimal dayRate, BigDecimal totalGrossMargin, BigDecimal totalPrice, BigDecimal totalCost, DashboardProjectDTO[] projects) {
        this.name = name;
        this.dayRate = dayRate;
        this.totalGrossMargin = totalGrossMargin;
        this.totalPrice = totalPrice;
        this.totalCost = totalCost;
        this.projects = projects;
    }

    //#region Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal getTotalGrossMargin() {
        return totalGrossMargin;
    }

    public void setTotalGrossMargin(BigDecimal totalGrossMargin) {
        this.totalGrossMargin = totalGrossMargin;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public DashboardProjectDTO[] getProjects() {
        return projects;
    }

    public void setProjects(DashboardProjectDTO[] projects) {
        this.projects = projects;
    }
    //#endregion
}
