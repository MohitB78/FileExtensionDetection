package com.example.demo.Controller;

import com.example.demo.Service.FileControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FileControllerService fileControllerService;

    @PostMapping("/detect")
    public ResponseEntity<Map<String, String>> detectFile(
            @RequestParam("file") MultipartFile file,String userId) {
        try {
            ResponseEntity<String> result = ResponseEntity.ok(fileControllerService.findExtensionOfFile(file));
            String fileName = file.getOriginalFilename();
            Map<String, String> res = new HashMap<>();
            String Extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            res.put("Found Extension", Extension);
            res.put("Desired Extension", result.getBody());

            if (Extension.equals(result.getBody())) {
                if(itHappensToBeInList(userId,result.getBody())) {
                    res.put("Status", "Valid");
                    return new ResponseEntity<>(res, HttpStatus.OK);
                }else{
                    res.put("Error","Not allowed to upload this type of file");
                    return new ResponseEntity<>(res,HttpStatus.FORBIDDEN);
                }
            }
            res.put("Status", "Not Valid");
            return new ResponseEntity<>(res, HttpStatus.NOT_ACCEPTABLE);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private boolean itHappensToBeInList(String userId, String desiredExtension) {
        Query query = new Query(Criteria.where("userId").is(userId));
        Map<String, Object> userPermission = mongoTemplate.findOne(query, Map.class, "extensionPermission");

        if (userPermission == null) {
            return false;
        }

        List<String> extensionsAllowed = (List<String>) userPermission.get("extensionsAllowed");
        return extensionsAllowed == null || extensionsAllowed.isEmpty() || extensionsAllowed.contains(desiredExtension);
    }
}
