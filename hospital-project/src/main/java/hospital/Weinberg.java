package hospital;

import Staff.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Weinberg implements Area {
    public int numRooms, numNurses, numResidents, numSolo, numAttending;
    public ArrayList<Pair> roomConfig;

    public Weinberg(int numRooms) {
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
        return numSolo <= 3;
    }

    @Override
    public boolean isPreferred() {
        if(!isValid()) return false;
        if (!isEven(numNurses) && !isEven(numResidents)) {
            // acceptable
            if (numNurses > 6) return false;
        } else if (isEven(numNurses) && isEven(numResidents)) {
            // acceptable
            if (numNurses >= 6) return false;
        }
        return true;
    }

    @Override
    public int calculateCost() {
        return reqAttending() + numSolo;
    }

    public int reqAttending() {
        if (!isEven(numNurses) && !isEven(numResidents)) {
            // acceptable
            if (numNurses > 6) return 2 + ((numNurses - 6) + numResidents) / 2;
            // preferred
            return (numNurses + numResidents) / 2;
        } else if (isEven(numNurses) && isEven(numResidents)) {
            // acceptable
            if (numNurses >= 6) return 2 + ((numNurses - 6) + numResidents) / 2;
            // preferred
            return (numNurses + numResidents) / 2;
        } else {
            return 1 + (numNurses - 3 + numResidents) /2;
        }
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
        return "WEINBERG";
    }

    @Override
    public int[] maxConstraint(){
        return new int[]{9999, 9999, 9999};
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

        int numThreeToOne;
        if (isEven(numNurses) == isEven(numResidents)) {
            if (numNurses > 6) {
                numThreeToOne = 2;
            } else {
                numThreeToOne = 0;
            }
        } else {
            numThreeToOne = 1;
        }

        for (int i = 1; i <= numAttending; i++) {
            if (i <= numThreeToOne) {
                attending.add(new Attending(name() + "_attending" + i));
                attending.add(new Attending(name() + "_attending" + i));
                attending.add(new Attending(name() + "_attending" + i));
            } else {
                attending.add(new Attending(name() + "_attending" + i));
                attending.add(new Attending(name() + "_attending" + i));
            }
        }
        for (Pair pair : roomConfig) {
            if (pair.getFaculty() == null) {
                pair.setFaculty(attending.poll());
            }
        }
    }

    private int mod3(int num) {
        return num % 3 >= 0 ? num % 3 : num % 3 + 3;
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
