package com.example.Uploadingfiles.utils.Search;

import com.example.Uploadingfiles.utils.exceptions.SolutionException;
import com.example.Uploadingfiles.utils.hospital.*;

import java.util.*;

public class TabuSearchDriver {
    public static Random rand = new Random();
    public int nurse, resident, solo, total;
    public int[] archive;
    public PriorityQueue<Area> areas;
    public Stack<Area> attempts;
    public ArrayList<Area> areaList;
    public PriorityQueue<Solution> solutionList;
    public int trials = 0;

    public TabuSearchDriver(int numNurse, int numRes, int numTotal) {
        nurse = numNurse;
        resident = numRes;
        total = numTotal;
        solo = total > (numNurse + numRes) ? total - (numNurse + numRes) : 0;
        archive = new int[]{nurse, resident, solo};
        areas = new PriorityQueue<>(new AreaComparator());
        attempts = new Stack<>();
        areaList = new ArrayList<>();
        solutionList = new PriorityQueue<>();
    }

    public void addArea(ArrayList<Area> insertAtOnce) {
        for (Area area : insertAtOnce) {
            addArea(area);
        }
    }

    public void addArea(Area[] insertAtOnce) {
        for (Area area : insertAtOnce) {
            addArea(area);
        }
    }

    public void addArea(Area A) {
        areas.add(A);
        areaList.add(A);
    }


    public Solution findSolution() throws SolutionException{
        while(!areas.isEmpty()) {
            if (trials > 999) throw new SolutionException("Solution not found");
            trials++;
            Area curr = areas.poll();
            // System.out.println(curr.name());
            try {
                findValidSolution(curr);
                // System.out.println(curr.name() + " " + curr);
                this.nurse -= curr.get(0);
                this.resident -= curr.get(1);
                this.solo -= curr.get(2);
                attempts.add(curr);
            } catch (SolutionException ex) {
                try {
                    Area previous = attempts.pop();
                    undo(previous);
                    areas.add(previous);
                    curr.reset();
                    areas.add(curr);
                } catch (EmptyStackException e) {
                    throw new SolutionException("Wrong first try");
                }
            }
        }

        return new Solution(getTotalCost(), areaList);
    }

    public Solution findSolutionForSure() throws SolutionException {
        Integer isImpossible = 0;
        while(isImpossible < 999) {
            try {
                findSolution();
                Solution solution = new Solution(getTotalCost(), areaList);
                return solution;
            } catch (SolutionException ex) {
                reset();
                isImpossible++;
            }
        }
        throw new SolutionException("There is no possible solution");
    }

    private void setToSolution(Solution solution) {
        nurse = archive[0];
        resident = archive[1];
        solo = archive[2];
        attempts.empty();
        areas = new PriorityQueue<>(new AreaComparator());
        areaList = new ArrayList<>();
        this.trials = 0;
        for (Area a: solution.archive) {
            areaList.add(a);
            areas.add(a);
        }
    }

    public Solution TabuSearch() {
        List<Solution> TabuList = new ArrayList<>();
        Solution current = findSolutionForSure();
        for(int i = 0; i < 100; i++) {
            findNeighbor(current);
            Solution best = solutionList.poll();
            while (TabuList.contains(best)) {
                solutionList.remove(best);
                best = solutionList.poll();
            }
            if (best == null) continue;
            TabuList.add(best);
            current = best;
        }
        return current;
    }

    private void findNeighbor(Solution current) {
        solutionList = new PriorityQueue<>();
        for (int i = 0; i < 100; i++) {
            setToSolution(current);
            List<Area> twoAreas = getRandomElement(areaList, 2);
            Area swap1 = twoAreas.get(0);
            Area swap2 = twoAreas.get(1);
            randomSwap(swap1, swap2);
            if (isValid()) {
                Solution neighbor = new Solution(getTotalCost(), areaList);
                solutionList.add(neighbor);
            }
        }
    }

    private boolean isValid() {
        for (Area a: areaList) {
            if(!a.isValid()){
                return false;
            }
        }
        return true;
    }

    private List<Area> getRandomElement(List<Area> cp, int totalItems)
    {
        Random rand = new Random();
        List<Area> list = new ArrayList<>();
        for (Area a : cp) {
            list.add(a);
        }

        // create a temporary list for storing
        // selected element
        List<Area> newList = new ArrayList<>();
        for (int i = 0; i < totalItems; i++) {

            // take a random index between 0 to size
            // of given List
            int randomIndex = rand.nextInt(list.size());

            // add element in temporary list
            newList.add(list.get(randomIndex));

            // Remove selected element from original list
            list.remove(randomIndex);
        }
        return newList;
    }

    private void randomSwap(Area area1, Area area2) {
        Random rand = new Random();
        List<Integer> staffToChooseFrom1 = new ArrayList<>();
        List<Integer> staffToChooseFrom2 = new ArrayList<>();
        // Down 1 in area1, Up 1 in area2
        if (area1.get(0) != 0) staffToChooseFrom1.add(0);
        if (area1.get(1) != 0) staffToChooseFrom1.add(1);
        if (area1.get(2) != 0) staffToChooseFrom1.add(2);
        // Down 1 in area2, Up 1 in area 1
        if (area2.get(0) != 0) staffToChooseFrom2.add(0);
        if (area2.get(1) != 0) staffToChooseFrom2.add(1);
        if (area2.get(2) != 0) staffToChooseFrom2.add(2);

        int swapIndex1 = staffToChooseFrom1.get(rand.nextInt(staffToChooseFrom1.size()));
        staffToChooseFrom2.remove((Object) swapIndex1);
        if (staffToChooseFrom2.size() == 0) return;
        int swapIndex2 = staffToChooseFrom2.get(rand.nextInt(staffToChooseFrom2.size()));

        int swap1[] = new int[]{0, 0, 0};
        int swap2[] = new int[]{0, 0, 0};
        swap1[swapIndex1] = -1;
        swap1[swapIndex2] = 1;
        swap2[swapIndex1] = 1;
        swap2[swapIndex2] = -1;

        area1.addStaff(swap1);
        area2.addStaff(swap2);
    }
    public Solution findBestSolution(int trials, boolean prioritizeCost)  {
        // Heuristic
        if (nurse + resident < total*0.5) {
            System.out.println("There is no possible solution");
            return null;
        }
        for (int i = 0; i < trials; i++){
            if (i % 100 == 0) System.out.println("Computing " + i + "th solution ... ");
            try {
                findSolutionForSure();
            } catch (SolutionException e) {
                System.out.println("There is no possible solution");
                return null;
            }
            Solution solution;
            if (prioritizeCost) solution = new Solution(getTotalCost(), areaList);
            else solution = new Solution(getScore(), areaList);
            solutionList.add(solution);
        }
        return solutionList.poll();
    }

    private void reset() {
        nurse = archive[0];
        resident = archive[1];
        solo = archive[2];
        attempts.empty();
        areas = new PriorityQueue<>(new AreaComparator());
        this.trials = 0;
        for (Area a: areaList) {
            a.reset();
            areas.add(a);
        }
    }

    private void undo(Area A) {
        this.nurse += A.get(0);
        this.resident += A.get(1);
        this.solo += A.get(2);
        A.reset();
    }

    private void findValidSolution(Area A) throws SolutionException {
        int count = 0;
        while (!A.isValid()) {
            if (count > 50) {
                throw new SolutionException("no valid solution");
            }
            A.reset();
            int nurse = this.nurse;
            int resident = this.resident;
            int solo = this.solo;
            int[] noValidSolution = {0, 0, 0};
            int i = 0;
            while (A.numEmpty() > 0) {
                if (i > 50) break;
                if (Arrays.equals(noValidSolution, new int[]{1, 1, 1})) {
                    throw new SolutionException("no valid solution");
                }
                if (i % 3 == 0 && noValidSolution[0] != 1) {
                    if (nurse == 0) {
                        noValidSolution[0] = 1;
                    }
                    // nurse
                    try {
                        int num = rand.nextInt(min(A.numEmpty(), nurse, A.maxConstraint()[0])) + 1;
                        A.addStaff(new int[]{num, 0, 0});
                        nurse -= num;
                        // System.out.println(A);
                    } catch (IllegalArgumentException ex) {
                        noValidSolution[0] = 1;
                    }
                } else if (i % 3 == 1 && noValidSolution[1] != 1) {
                    if (resident == 0) {
                        noValidSolution[0] = 1;
                    }
                    // resident
                    try {
                        int num = rand.nextInt(min(A.numEmpty(), resident, A.maxConstraint()[1])) + 1;
                        A.addStaff(new int[]{0, num, 0});
                        resident -= num;
                    } catch (IllegalArgumentException ex) {
                        noValidSolution[1] = 1;
                    }
                } else if (i % 3 == 2 && noValidSolution[2] != 1){
                    if (solo == 0) {
                        noValidSolution[0] = 1;
                    }
                    // solo
                    try {
                        int num = rand.nextInt(min(A.numEmpty(), solo, A.maxConstraint()[2])) + 1;
                        A.addStaff(new int[]{0, 0, num});
                        solo -= num;
                    } catch (IllegalArgumentException ex) {
                        noValidSolution[2] = 1;
                    }
                }
                i++;
            }
            count++;
        }
    }

    public void printResult() {
        for (Area A : areaList) {
            evaluateSolution(A);
        }
    }

    public void evaluateSolution(Area A) {
        if(A.isValid()){
            System.out.println("The solution for " + A.name() + " is valid");
            System.out.println(A);
            System.out.println("The cost is: " + A.calculateCost());
            if(A.isPreferred()) {
                System.out.println("The solution for " + A.name() + " is preferred");
            } else {
                System.out.println("The solution for " + A.name() + " is not preferred");
            }
        } else {
            System.out.println("The solution " + A.name() + " is not valid");
        }
    }


    private static class AreaComparator implements Comparator<Area> {
        // Overriding compare() method of Comparator
        // comparison based on midterm grade
        public int compare(Area a1, Area a2) {
            return a1.numEmpty() - a2.numEmpty();
        }
    }


    private static class AreaComparatorBasedOnConstraints implements Comparator<Area> {
        // Overriding compare() method of Comparator
        // comparison based on midterm grade
        public int compare(Area a1, Area a2) {
            return a1.getPriority() - a2.getPriority();
        }
    }


    private int min(int i, int j, int k) {
        int min = Math.min(i, j);
        min = Math.min(min, k);
        return min;
    }

    public int getTotalCost() {
        int cost = 0;
        for (Area A: areaList) {
            cost += A.reqAttending();
        }
        return cost;
    }

    public int getScore() {
        int cost = 0;
        for (Area A: areaList) {
            cost += A.getScore();
        }
        return cost;
    }


}
