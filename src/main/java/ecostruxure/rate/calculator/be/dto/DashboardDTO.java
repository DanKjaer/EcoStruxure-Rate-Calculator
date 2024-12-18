package ecostruxure.rate.calculator.be.dto;

import java.math.BigDecimal;

public class DashboardDTO {
    private String name;
    private BigDecimal dayRate;
    private BigDecimal grossMargin;
    private BigDecimal totalPrice;
    private DashboardProjectDTO[] projects;
    private DashboardTeamDTO[] teams;

    public DashboardDTO(String name,
                        BigDecimal dayRate,
                        BigDecimal totalGrossMargin,
                        BigDecimal totalPrice,
                        DashboardProjectDTO[] projects,
                        DashboardTeamDTO[] teams) {
        this.name = name;
        this.dayRate = dayRate;
        this.grossMargin = totalGrossMargin;
        this.totalPrice = totalPrice;
        this.projects = projects;
        this.teams = teams;
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

    public BigDecimal getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(BigDecimal grossMargin) {
        this.grossMargin = grossMargin;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public DashboardProjectDTO[] getProjects() {
        return projects;
    }

    public void setProjects(DashboardProjectDTO[] projects) {
        this.projects = projects;
    }

    public DashboardTeamDTO[] getTeams() {
        return teams;
    }

    public void setTeams(DashboardTeamDTO[] teams) {
        this.teams = teams;
    }
    //#endregion
}
