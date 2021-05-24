package Staff;

public class Pair {
    Staff staff;
    Staff faculty;

    public Pair(Staff staff, Staff faculty) {
        this.staff = staff;
        this.faculty = faculty;
    }

    @Override
    public String toString() {
        if (staff == null) {
            return "No staff "+ faculty.getType() + ": " + faculty.toString();
        } else if (faculty == null) {
            return staff.getType() + ": " + staff.toString() + " No faculty";
        } else {
            return staff.getType() + ": " + staff.toString() + " " + faculty.getType() + ": " + faculty.toString();

        }
    }

    public void setFaculty(Attending a) {
        this.faculty = a;
    }

    public Staff getFaculty() {
        return this.faculty;
    }

    public Staff getStaff() {
        return this.staff;
    }
}