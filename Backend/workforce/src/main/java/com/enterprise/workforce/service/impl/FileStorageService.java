package com.enterprise.workforce.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadBaseDir;

    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final List<String> ALLOWED_TYPES = List.of("image/png", "image/jpeg", "image/jpg", "image/webp");

    public String storeEmployeeFile(Long employeeId, MultipartFile file) {
        validateFile(file);

        try {
            // Create folder per employee
            Path employeeDir = Paths.get(uploadBaseDir, "employees", String.valueOf(employeeId))
                    .toAbsolutePath().normalize();
            Files.createDirectories(employeeDir);

            // Generate UUID filename
            String originalName = Objects.requireNonNull(file.getOriginalFilename());
            String ext = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + ext;

            Path targetPath = employeeDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "employees/" + employeeId + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store employee file", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("File too large (max 2MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("Invalid file type. Allowed: PNG, JPG, JPEG, WEBP");
        }
    }

    public Resource loadFile(String relativePath) {
        try {
            Path filePath = Paths.get(uploadBaseDir).resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found: " + relativePath);
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new RuntimeException("Error loading file", e);
        }
    }
}
