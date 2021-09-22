package com.example.Uploadingfiles.utils.hospital;

import com.example.Uploadingfiles.utils.Staff.Pair;

import java.util.ArrayList;

/**
 * Area ADT to represent hospital area
 */
public interface Area {
    boolean isValid();
    boolean isPreferred();
    int calculateCost();
    int numEmpty();
    void addStaff(int staff[]);
    void reset();
    int get(int index) throws IllegalArgumentException;
    String name();
    int[] maxConstraint();
    void roomConfig();
    int getPriority();
    int getScore();
    ArrayList<Pair> getRoomConfig();
    int reqAttending();
}
