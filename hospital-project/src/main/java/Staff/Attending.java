package Staff;

public class Attending implements Staff{
    String name;

    public Attending(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
    @Override
    public String getType() {
        return "Attending";
    }
    @Override
    public boolean isFaculty() {
        return true;
    }


}
