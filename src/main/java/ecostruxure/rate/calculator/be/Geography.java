package ecostruxure.rate.calculator.be;

public class Geography {
    private int id;
    private String name;
    private boolean predefined;

    public Geography() {}

    public Geography(int id, String name, boolean predefined) {
        this.id = id;
        this.name = name;
        this.predefined = predefined;
    }

    public Geography(String name) {
        this(0, name, false);
    }

    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public boolean predefined() {
        return predefined;
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

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }

    @Override
    public String toString() {
        return "Geography{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", predefined=" + predefined +
                '}';
    }
}
