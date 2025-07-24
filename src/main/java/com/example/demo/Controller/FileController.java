package com.example.demo.Controller;

import com.example.demo.Service.FileControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileControllerService fileControllerService;

    @PostMapping("/detect")
    public ResponseEntity<Map<String, String>> detectFile(
            @RequestParam("file") MultipartFile file) {
        try {
            String result = fileControllerService.findExtensionOfFile(file);
            String fileName = file.getOriginalFilename();
            Map<String, String> res = new HashMap<>();
            String Extension = fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
            res.put("FileName", fileName);
            res.put("Desired Extension", result);
            res.put("Found Extension",Extension);
//            System.out.println("FoundExtension: "+Extension);
//            System.out.println("Desired Extension: "+result);
            if(Extension.isEmpty())return new ResponseEntity<>(res,HttpStatus.NOT_ACCEPTABLE);

            if (areExtensionsEquivalent(result,Extension)) {
                res.put("Status", "Valid");
                System.out.print(res.toString());
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
            System.out.print(res.toString());
            res.put("Status", "Not Valid");
            return new ResponseEntity<>(res, HttpStatus.NOT_ACCEPTABLE);
        } catch (IOException e) {
            Map<String, String> newResponseMap = new HashMap<>();
            newResponseMap.put("Input Output Exception Error", "Input Output Exception Occurred when Detecting an Extension of File");
            return new ResponseEntity<>(newResponseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.out.println(e + " Error in File Extension Detection");
            Map<String, String> newResponseMap = new HashMap<>();
            newResponseMap.put("Error", "Something Went wrong when detecting the File Extension");
            return new ResponseEntity<>(newResponseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // same may have the same signature or magic number such as jpg and jpeg have same magic numbers and the previous code will fail for that for I am validating some common files whose magic numbers are same.
    private boolean areExtensionsEquivalent(String ext1, String ext2) {
        if (ext1 == null || ext2 == null) return false;

        String e1 = ext1.toLowerCase();
        String e2 = ext2.toLowerCase();

        // Treat jpg and jpeg as the same
        if ((e1.equals("jpg") || e1.equals("jpeg")) &&
                (e2.equals("jpg") || e2.equals("jpeg"))) {
            return true;
        }

        return e1.equals(e2);
    }
}
