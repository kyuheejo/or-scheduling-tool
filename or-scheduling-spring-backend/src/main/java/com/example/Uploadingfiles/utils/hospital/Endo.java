package com.example.Uploadingfiles.utils.hospital;

import com.example.Uploadingfiles.utils.Staff.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Endo implements Area {
    public int numRooms, numNurses, numResidents, numSolo, numAttending;
    public ArrayList<Pair> roomConfig;

    public Endo(int numRooms) {
        this.numRooms = numRooms;
        numNurses = 0;
        numResidents = 0;
        numSolo = 0;
        roomConfig = new ArrayList<>();
    }

    @Override
    public int getPriority() {
        return numEmpty();
    }

    @Override
    public int getScore() {
        if (isPreferred()) return (int) (calculateCost()*0.5);
        return calculateCost();
    }

    @Override
    public boolean isValid() {
        if (numEmpty() != 0) return false;
        if (numResidents > 2) return false;
        if (numSolo != 0) return false;
        return numRooms > 3 || numResidents == 0;
    }

    @Override
    public boolean isPreferred() {
        if(!isValid()) return false;
        return numResidents <= 1;
    }

    @Override
    public int calculateCost() {
        return reqAttending() + numSolo;
    }

    public int reqAttending() {
        if (isEven(numRooms)) return (numNurses + numResidents) / 2;
        else return 1 + (numNurses - 3 + numResidents) / 2;
    }

    private boolean isEven(int i) {
        return i % 2 == 0;
    }

    @Override
    public int numEmpty() {
        return numRooms - (numNurses + numResidents + numSolo);
    }

    @Override
    public void addStaff(int staff[]) {
        // 0: nurse
        numNurses += staff[0];
        // 1: residents
        numResidents += staff[1];
        // 2: solo
        numSolo += staff[2];
    }

    @Override
    public void reset() {
        numNurses = 0;
        numResidents = 0;
        numSolo = 0;
        roomConfig = new ArrayList<>();
    }

    @Override
    public int get(int index) throws IllegalArgumentException {
        if (index == 0) return numNurses;
        if (index == 1) return numResidents;
        if (index == 2) return numSolo;
        else throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return "Total rooms: " + numRooms + " CRNAs: " + numNurses + " Residents: " + numResidents + " Solo: " + numSolo;
    }

    @Override
    public String name() {
        return "ENDO";
    }

    @Override
    public int[] maxConstraint(){
        return new int[]{9999, 2, 0};
    }

    @Override
    public void roomConfig() {
        //TODO: fix room Config

        roomConfig = new ArrayList<>();
        numAttending = reqAttending();

        for (int i = 1; i <= numNurses; i++) {
            Staff nurse = new Nurse(name() + "_CRNA" + i);
            roomConfig.add(new Pair(nurse, null));
        }

        for (int i = 1; i <= numResidents; i++) {
            Staff resident = new Resident(name() + "_resident" + i);
            roomConfig.add(new Pair(resident, null));
        }

        for (int i = 1; i <= numSolo; i++) {
            Staff solo = new Attending(name() + "_solo" + i);
            roomConfig.add(new Pair(null, solo));
        }

        Queue<Attending> attending = new LinkedList<>();

        for (int i = 1; i <= numAttending; i++) {
            if (!isEven(numRooms) && i == 1) {
                attending.add(new Attending(name() + "_attending" + i));
                attending.add(new Attending(name() + "_attending" + i));
                attending.add(new Attending(name() + "_attending" + i));
            } else {
                attending.add(new Attending(name() + "_attending" + i));
                attending.add(new Attending(name() + "_attending" + i));
            }
        }

        for (Pair pair: roomConfig) {
            if (pair.getFaculty() == null) {
                pair.setFaculty(attending.poll());
            }
        }
    }

    public void printRoomConfig() {
        roomConfig();
        for(Pair pair : roomConfig) {
            System.out.println(pair);
        }
    }

    @Override
    public ArrayList<Pair> getRoomConfig() {
        roomConfig();
        return roomConfig;
    }


}
