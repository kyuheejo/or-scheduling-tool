package hospital;

import Staff.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AdultRemote implements Area {
    public int numRooms, numNurses, numResidents, numSolo, numAttending;
    public ArrayList<Pair> roomConfig;
    public String name;

    public AdultRemote(int numRooms, String name) {
        this.numRooms = numRooms;
        numNurses = 0;
        numResidents = 0;
        numSolo = 0;
        this.name = name;
    }

    @Override
    public int getScore() {
        return reqAttending();
    }

    @Override
    public int getPriority() {
        return numEmpty();
    }

    @Override
    public boolean isValid() {
        if (numEmpty() != 0) return false;
        if (numResidents != 0) return false;
        return numSolo == 0;
    }

    @Override
    public boolean isPreferred() {
        return isValid();
    }

    @Override
    public int calculateCost() {
        return numNurses;
    }

    @Override
    public int reqAttending() { return numNurses; }


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
        return name;
    }

    @Override
    public int[] maxConstraint(){
        return new int[]{9999, 0, 0};
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

        Queue<Attending> attending = new LinkedList<>();
        for (int i = 1; i <= numAttending; i++) {
            attending.add(new Attending(name() + "_attending" + i));
        }

        for (Pair pair: roomConfig) {
            if (pair.getFaculty() == null) {
                pair.setFaculty(attending.poll());
            }
        }
    }

    @Override
    public ArrayList<Pair> getRoomConfig() {
        roomConfig();
        return roomConfig;
    }

}
