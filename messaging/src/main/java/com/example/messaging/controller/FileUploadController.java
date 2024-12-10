package com.example.messaging.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:3000") // Allow React app to connect
public class FileUploadController {

    private static final String UPLOAD_DIR = "uploads/"; // Path to the uploads folder, relative to the project directory

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Get the absolute path for the uploads directory in the project directory
            String uploadDirPath = new File(UPLOAD_DIR).getAbsolutePath();

            // Create the uploads directory if it doesn't exist
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the file to the directory
            File dest = new File(uploadDirPath + File.separator + file.getOriginalFilename());
            file.transferTo(dest);

            return "File uploaded successfully: " + file.getOriginalFilename();
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        }
    }
}
