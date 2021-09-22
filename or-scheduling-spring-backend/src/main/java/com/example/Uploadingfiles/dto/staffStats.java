package com.example.Uploadingfiles.dto;

public class staffStats {
    private int numCrna;
    private int numResidents;

    public staffStats(int numCrna, int numResidents) {
        this.numCrna = numCrna;
        this.numResidents = numResidents;
    }

    public int getNumCrna() {
        return numCrna;
    }

    public int getNumResidents() {
        return numResidents;
    }
}
