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
            ResponseEntity<String> result = ResponseEntity.ok(fileControllerService.findExtensionOfFile(file));
            String fileName = file.getOriginalFilename();
            Map<String, String> res = new HashMap<>();
            String Extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            res.put("Found Extension", Extension);
            res.put("Desired Extension", result.getBody());

            if (Extension.equals(result.getBody())) {
                res.put("Status", "Valid");
                return new ResponseEntity<>(res, HttpStatus.OK);
            }
            res.put("Status", "Not Valid");
            return new ResponseEntity<>(res, HttpStatus.NOT_ACCEPTABLE);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
