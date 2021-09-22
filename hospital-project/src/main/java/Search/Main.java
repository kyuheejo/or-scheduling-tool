package Search;

import Search.Excel;
import Search.Solution;
import Search.TabuSearchDriver;
import exceptions.SolutionException;

import java.io.IOException;
import java.nio.file.*;
import java.util.Random;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        // Set inputPath to the empty excel file (existing file)
        // Set outputPath to the filepath & filename you want to save the output at
        // (doesn't matter whether the file exists)
        String inputPath = "/Users/namseunghyeon/Documents/OR_scheduling_project/hospital-project-excel/test_v3.xlsx";
        String outputPath = "/Users/namseunghyeon/Documents/OR_scheduling_project/hospital-project-excel/test_v3_out.xlsx";

        Scanner scanner = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter number of CRNAs: ");
        Integer numNurse = Integer.valueOf(scanner.nextLine());
        System.out.println("Enter number of Residents: ");
        Integer numRes = Integer.valueOf(scanner.nextLine());

        // Create Excel object
        Excel excel = new Excel(inputPath);
        TabuSearchDriver driver = new TabuSearchDriver(numNurse, numRes,  excel.getTotal());
        driver.addArea(excel.getAreaList());
        // Solution solution = driver.findBestSolution(9999, true);
        Solution solution = driver.TabuSearch();
        // Print solution
        System.out.println(solution);
        // Print room configuration
        // solution.printRoomConfig();

        // Copy original file to outputPath
        FileSystem system = FileSystems.getDefault();
        Path original = system.getPath(inputPath);
        Path target = system.getPath(outputPath);
        try {
            // Throws an exception if the original file is not found.
            Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println("ERROR");
        }

        // Write solution to that file
        excel.writeSolution(solution, outputPath);
    }

}
