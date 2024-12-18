package ecostruxure.rate.calculator.be.dto;

public class DashboardTeamDTO {
    private String name;

    public DashboardTeamDTO(String name) {
        this.name = name;
    }

    //region Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //endregion
}
