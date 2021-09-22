package com.example.Uploadingfiles;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.example.Uploadingfiles.utils.Search.Excel;
import com.example.Uploadingfiles.utils.Search.Solution;
import com.example.Uploadingfiles.utils.Search.TabuSearchDriver;
import com.example.Uploadingfiles.utils.Search.solutionNode;
import com.example.Uploadingfiles.utils.exceptions.SolutionException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Uploadingfiles.storage.StorageFileNotFoundException;
import com.example.Uploadingfiles.storage.StorageService;


@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(Integer numCrna, Integer numResidents, @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, Model model) throws IOException {

        storageService.store(file);

        String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3
        XSSFWorkbook workbook = null;

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } 

        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Sheet worksheet = workbook.getSheetAt(0);
        Excel excel = new Excel(worksheet);



        TabuSearchDriver driver = new TabuSearchDriver(numCrna, numResidents,  excel.getTotal());
        driver.addArea(excel.getAreaList());
        try {
            Solution solution = driver.TabuSearch();
            // Print solution
            List<solutionNode> solutionsNodeList = solution.getSolutionNodes();
            redirectAttributes.addFlashAttribute("solutionList", solutionsNodeList);
            redirectAttributes.addFlashAttribute("message",
                    "We got a solution!");
        }catch(SolutionException e) {
            // Print solution
            redirectAttributes.addFlashAttribute("message",
                    "No possible solution. Maybe try again!");

        }




        //redirectAttributes.addFlashAttribute("message",
        //        "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}