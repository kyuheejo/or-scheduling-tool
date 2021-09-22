package com.example.Uploadingfiles.utils.Staff;

public class Resident implements Staff {
    String name;

    public Resident(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
    @Override
    public String getType() {
        return "Resident";
    }
    @Override
    public boolean isFaculty() {
        return false;
    }
}
