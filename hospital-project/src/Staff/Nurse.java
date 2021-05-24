package Staff;

public class Nurse implements Staff {
    String name;

    public Nurse(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
    @Override
    public String getType() {
        return "CRNA";
    }

    @Override
    public boolean isFaculty() {
        return false;
    }
}
