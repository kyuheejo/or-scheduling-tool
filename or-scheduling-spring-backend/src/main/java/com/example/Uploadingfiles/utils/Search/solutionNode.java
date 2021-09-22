package com.example.Uploadingfiles.utils.Search;

import com.example.Uploadingfiles.utils.Staff.Pair;
import com.example.Uploadingfiles.utils.hospital.Area;

import java.util.ArrayList;
import java.util.Objects;

public class solutionNode implements Comparable<solutionNode> {
    public int nurse, resident, solo, attending;
    public boolean isPreferred;
    public ArrayList<Pair> roomConfig;
    String name;

    solutionNode(Area A) {
        nurse = A.get(0);
        resident = A.get(1);
        solo = A.get(2);
        attending = A.reqAttending();
        name = A.name();
        isPreferred = A.isPreferred();
        roomConfig = A.getRoomConfig();
    }

    solutionNode(String name, int numNurse, int numResidents, int numSolo, int numAttending) {
        nurse = numNurse;
        resident = numResidents;
        solo = numSolo;
        attending = numAttending;
        this.name = name;
    }

    solutionNode(solutionNode cp) {
        nurse = cp.nurse;
        resident = cp.resident;
        solo = cp.solo;
        attending = cp.attending;
        name = cp.name;
        isPreferred = cp.isPreferred;
        roomConfig = cp.roomConfig;
    }

    public int getNurse() {
        return nurse;
    }

    public int getResident() {
        return resident;
    }

    public int getSolo() {
        return solo;
    }

    public int getAttending() {
        return attending;
    }

    public boolean isPreferred() {
        return isPreferred;
    }

    @Override
    public String toString() {
        String returnString = name + " CRNA: " + nurse + " Residents: " + resident + " Solo: " + solo + " Attending: " + attending;
        if (isPreferred) returnString += " is a preferred solution";
        return returnString;
    }

    public void printRoomConfig() {
        for(Pair pair : roomConfig) {
            System.out.println(pair);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        solutionNode that = (solutionNode) o;
        return nurse == that.nurse && resident == that.resident && solo == that.solo && attending == that.attending && isPreferred == that.isPreferred && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nurse, resident, solo, attending, isPreferred, name);
    }

    // Sort solution nodes by name
    @Override
    public int compareTo(solutionNode s) {
        return this.name.compareTo(s.getName());
    }

    public String getName() {
        return name;
    }

    public ArrayList<Pair> getRoomConfig() {
        return roomConfig;
    }
}