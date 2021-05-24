package Search;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hospital.*;
import Staff.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.*;

import javax.management.InstanceNotFoundException;


public class Excel {
    public ArrayList<Area> areaList;
    private Workbook workbook;
    private Sheet sheet;
    private Map<String, Integer> lookup;
    private Integer total;
    public static String FILENAME;

    public Excel(String filepath){
        areaList = new ArrayList<>(20);
        total = 0;
        try {
            workbook = WorkbookFactory.create(new File(filepath));
            sheet = workbook.getSheetAt(0);
        } catch(Exception e) {
            System.out.println("File not found");
        }
        FILENAME = filepath;
        lookup = new HashMap<>();
        lookup.put("ZBOR 3", 19);
        lookup.put("WEINBERG", 16);
        lookup.put("RAD4", 0);
        lookup.put("Perc Trach", 0);
        lookup.put("Nel Bas", 0);
        lookup.put("RadOnc", 0);
        lookup.put("ECT", 0);
        lookup.put("ENDO", 9);
        lookup.put("JHOC", 8);
        lookup.put("WILMER", 7);
        lookup.put("GSS", 9);
        initializeArea();
    }

    public void initializeArea() {
        for (Map.Entry<String,Integer> entry : lookup.entrySet()){
            Area a = null;
            String name = entry.getKey();
            int count = count(name);
            total += count;
            if (count != 0) {
                switch (name) {
                    case "ZBOR 3":  a = new Zayed(count); break;
                    case "WEINBERG": a = new Weinberg(count); break;
                    case "RAD4":
                    case "Perc Trach":
                    case "Nel Bas":
                    case "RadOnc":
                        a = new AdultRemote(count, name); break;
                    case "ECT":  a = new ECT(count); break;
                    case "ENDO":  a = new Endo(count); break;
                    case "JHOC":  a = new JHOC(count); break;
                    case "WILMER": a = new Wilmer(count); break;
                    case "GSS": a = new GSS(count); break;
                    default: System.out.println("No matching String");
                        break;
                }
                if (a != null) areaList.add(a);
            }
        }
    }


    public Integer count(String name) throws IllegalArgumentException {
        CellAddress target = findAddress(name);
        int count = 0;
        int startRow = target.getRow() + 1;
        int col = target.getColumn();
        int i = 1;
        if (name.equals("GSS")) i = 0;
        if (lookup.get(name) != 0) {
            for (; i <= lookup.get(name); i++) {
                Cell cell = sheet.getRow(startRow + i).getCell(col);
                CellStyle cellstyle = cell.getCellStyle();
                Color color = cellstyle.getFillForegroundColorColor();
                if (isWhite(color)) count++;
            }
        } else {
            Cell cell = sheet.getRow(target.getRow()).getCell(col + 1);
            CellStyle cellstyle = cell.getCellStyle();
            Color color = cellstyle.getFillForegroundColorColor();
            if (isWhite(color)) count++;
        }

        return count;
    }

    private boolean isWhite(Color color) {
        if (color == null) {
            return true;
        } else {
            String checkColor = "";
            if (color instanceof XSSFColor) {
                XSSFColor xssfColor = (XSSFColor)color;
                checkColor = xssfColor.getARGBHex();
                if (xssfColor.hasTint()) return false;
            } else if (color instanceof HSSFColor) {
                checkColor = ((HSSFColor) color).getHexString();
            }
            return checkColor.equals("FFFFFFFF");
        }
    }


    public CellAddress findAddress(String name) throws IllegalArgumentException{
        name = name.replaceAll("\\s","");
        CellAddress target = null;
        for (Row row : sheet) {
            for (Cell cell : row) {
                if(cell.getAddress().getColumn() > 10) continue;
                if (name.equals(String.valueOf(cell).replaceAll("\\s","")))
                    target = cell.getAddress();
                // System.out.println(cell.getAddress() + ": " + String.valueOf(cell));
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("Cannot find name");
        }
        return target;
    }

    public void writeSolution(Solution solution, String excelFilePath) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (solutionNode sol : solution.areas()) {
                Iterator itr = sol.getRoomConfig().iterator();
                CellAddress target = findAddress(sol.getName());
                int startRow = target.getRow() + 1;
                int col = target.getColumn();
                int i = 1;
                if (sol.name.equals("GSS")) i = 0;
                if (lookup.get(sol.name) != 0) {
                    for (; i <= lookup.get(sol.name); i++) {
                        Cell staffCell = sheet.getRow(startRow + i).getCell(col);
                        Cell facultyCell = sheet.getRow(startRow + i).getCell(col + 1);
                        CellStyle cellstyle = staffCell.getCellStyle();
                        Color color = cellstyle.getFillForegroundColorColor();
                        if (isWhite(color)) {
                            if (itr.hasNext()) {
                                Pair nextPair = (Pair) itr.next();
                                if (nextPair.getStaff() != null) staffCell.setCellValue(nextPair.getStaff().toString());
                                if (nextPair.getFaculty() != null) facultyCell.setCellValue(nextPair.getFaculty().toString());
                            }
                        }
                    }
                } else {
                    Cell staffCell = sheet.getRow(target.getRow()).getCell(col + 1);
                    Cell facultyCell = sheet.getRow(target.getRow()).getCell(col + 2);
                    CellStyle cellstyle = staffCell.getCellStyle();
                    Color color = cellstyle.getFillForegroundColorColor();
                    if (isWhite(color)) {
                        if(itr.hasNext()) {
                            Pair nextPair = (Pair) itr.next();
                            if (nextPair.getStaff() != null) staffCell.setCellValue(nextPair.getStaff().toString());
                            if (nextPair.getFaculty() != null) facultyCell.setCellValue(nextPair.getFaculty().toString());
                        }
                    }
                }
            }

            inputStream.close();
            FileOutputStream outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Area> getAreaList() {
        return areaList;
    }

    public Integer getTotal() {
        return total;
    }


    public static void main(String[] args) {
        Excel excel = new Excel("/Users/namseunghyeon/Documents/hospital-project-excel/test.xlsx");

    }

}