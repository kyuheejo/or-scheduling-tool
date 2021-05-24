package Search;

import Staff.Pair;
import hospital.Area;

import java.util.*;

public class Solution implements Comparable<Solution>{
    public int score;
    ArrayList<solutionNode> solution;
    ArrayList<Area> archive;

    /**
     * Default constructor
     * @param score
     * @param a
     */
    Solution(int score, ArrayList<Area> a) {
        this.score = score;
        solution = new ArrayList<>();
        for (Area area : a) {
            solution.add(new solutionNode(area));
        }
        // Sort solution by name for comparison
        Collections.sort(solution);
        archive = new ArrayList<>();
        for (Area area: a) {
            archive.add(area);
        }
    }

    /**
     * Copy construcor.
     * @param copy
     */
    public Solution(Solution copy) {
        this.score = copy.score;
        solution = new ArrayList<>();
        for (solutionNode sln : copy.solution) {
            solution.add(new solutionNode(sln));
        }
        // Sort solution by name for comparison
        Collections.sort(solution);
        archive = new ArrayList<>();
        for (Area area: copy.archive) {
            archive.add(area);
        }
    }


    @Override
    public String toString() {
        int nurse = 0;
        int resident = 0;
        int solo = 0;
        int attending = 0;
        String toReturn = "";
        for (solutionNode sln : solution) {
            toReturn += sln.toString() + "\n";
            nurse += sln.nurse;
            resident += sln.resident;
            solo += sln.solo;
            attending += sln.attending;
        }
        toReturn += "Solution Summary:\n";
        toReturn += "CRNAs: " + nurse + " Residents: " + resident + " Solo: " + solo + " Attending: " + attending + "\n";
        return toReturn;
    }

    public void printRoomConfig() {
        for (solutionNode sln : solution) {
            sln.printRoomConfig();
        }
    }

    @Override
    public int compareTo(Solution o){
        // the higher score, the better (in min heap)
        return score - o.score;
    }

    @Override
    public boolean equals(Object o) {
        // if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution rhs = (Solution) o;
        if (solution.size() != rhs.solution.size()) return false;
        Iterator itr = solution.iterator();
        Iterator ritr = rhs.solution.iterator();
        while(itr.hasNext() && ritr.hasNext()) {
            solutionNode s1 = (solutionNode) itr.next();
            solutionNode s2 = (solutionNode) ritr.next();
            if (!s1.equals(s2)) return false;
        }
        if (itr.hasNext() != ritr.hasNext()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(solution);
    }

    public Iterable<solutionNode> areas() {
        List<solutionNode> areaList = solution;
        return Collections.unmodifiableList(areaList);
    }
}
