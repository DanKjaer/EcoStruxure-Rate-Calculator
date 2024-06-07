package ecostruxure.rate.calculator.be;

public class ProfileData {
    private int id;
    private String name;
    private String currency;
    private int geography;
    private boolean overhead;
    private boolean archived;

    public ProfileData() {}

    public ProfileData(int id, String name, String currency, int geography,
                       boolean overhead, boolean archived) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.geography = geography;
        this.overhead = overhead;
        this.archived = archived;
    }

    public ProfileData(String name, String currency, int geography,
                       boolean overhead, boolean archived) {
        this.name = name;
        this.currency = currency;
        this.geography = geography;
        this.overhead = overhead;
        this.archived = archived;
    }

    public int id() {
        return this.id;
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

    public String currency() {
        return currency;
    }

    public void currency(String currency) {
        this.currency = currency;
    }

    public int geography() {
        return geography;
    }

    public void geography(int geography) {
        this.geography = geography;
    }

    public boolean overhead() {
        return overhead;
    }

    public void overhead(boolean overhead) {
        this.overhead = overhead;
    }

    public boolean archived() {
        return archived;
    }

    public void archived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public String toString() {
        return "ProfileData{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", geography=" + geography +
                ", overhead=" + overhead +
                ", archived=" + archived +
                '}';
    }
}
