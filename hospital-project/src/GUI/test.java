package GUI;
import Search.Excel;
import Search.Solution;
import Search.TabuSearchDriver;
import exceptions.SolutionException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class test {
    public static void main (String [] args) {
        test_Frame tf = new test_Frame();
    }
}

class test_Frame extends JFrame implements ActionListener{

    private JFileChooser jfc = new JFileChooser("/Users/namseunghyeon/Documents/OR_scheduling_project/hospital-project-excel/");
    private JButton jbt_open = new JButton("Select Input Path");
    // private JButton jbt_save = new JButton("Save");
    private JLabel jlb = new JLabel(" ");
    private JTextField textField = new JTextField();
    private JLabel clabel = new JLabel("Enter number of CRNAs:");
    private JTextField textField2 = new JTextField();
    private JLabel rlabel = new JLabel("Enter number of residents:");
    private JButton button = new JButton("Start Running");
    private JLabel status = new JLabel(" ");
    private Excel excel;
    private TabuSearchDriver driver;


    public test_Frame(){
        super("test");
        this.init();
        this.start();
        this.setSize(400,200);
        this.setVisible(true);
    }
    public void init(){
        getContentPane().setLayout(new FlowLayout());
        clabel.setHorizontalAlignment(SwingConstants.CENTER);
        clabel.setVerticalAlignment(SwingConstants.CENTER);
        add(clabel, BorderLayout.CENTER);
        textField.setPreferredSize(new Dimension(80, 20));
        add(textField, BorderLayout.CENTER);
        add(rlabel, BorderLayout.CENTER);
        textField2.setPreferredSize(new Dimension(80, 20));
        add(textField2, BorderLayout.CENTER);
        add(jbt_open, BorderLayout.CENTER);
        add(jlb);
        add(button, BorderLayout.CENTER);
        status.setPreferredSize(new Dimension(300, 100));
        add(status, BorderLayout.CENTER);
        // add(jbt_save);

    }
    public void start(){
        jbt_open.addActionListener(this);
        button.addActionListener(this);
        // jbt_save.addActionListener(this);

        jfc.setFileFilter(new FileNameExtensionFilter("xlsx", "xlsx"));
        // 파일 필터
        jfc.setMultiSelectionEnabled(false);//다중 선택 불가
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        if(arg0.getSource() == jbt_open){
            if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                // showopendialog 열기 창을 열고 확인 버튼을 눌렀는지 확인
                jlb.setText("input path: " + jfc.getSelectedFile().toString());
                // jfc.getSelectedFile().toString()

            }
        } else if (arg0.getSource() == button) {
            button.setText("clicked");
            Integer numNurse = Integer.valueOf(textField.getText());
            Integer numRes = Integer.valueOf(textField2.getText());
            String inputPath = jfc.getSelectedFile().toString();
            String outputPath = inputPath.replace(".xlsx", "_out.xlsx");
            status.setText("Successfully read input");

            // Create Excel object
            excel = new Excel(inputPath);
            status.setText("Created Excel object");
            status.setText("Total number of Operating Rooms: " + excel.getTotal());
            driver = new TabuSearchDriver(numNurse, numRes,  excel.getTotal());
            driver.addArea(excel.getAreaList());

            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Solution solution = driver.findBestSolution(9999, true);
                        Solution solution = driver.TabuSearch();
                        status.setText("Computed solution");

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
                    } catch (SolutionException ex) {
                        status.setText("There is no possible solution based on the given numbers");
                    }
                }
            });
            t.start();

        }
    }

}
