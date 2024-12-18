package ecostruxure.rate.calculator.be.dto;

public class GeographyDTO {
    private int id;
    private String name;

    public GeographyDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public GeographyDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
