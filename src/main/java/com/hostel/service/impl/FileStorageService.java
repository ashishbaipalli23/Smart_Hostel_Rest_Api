package com.hostel.service.impl;

import com.hostel.utils.FileEncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileEncryptionUtil fileEncryptionUtil;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    public FileStorageService(FileEncryptionUtil fileEncryptionUtil) {
        this.fileEncryptionUtil = fileEncryptionUtil;
    }

    public String storeFile(MultipartFile file, Long userId) throws Exception {
        log.info("Starting file storage for user ID: {}", userId);
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            log.warn("Upload rejected due to invalid content type: {}", file.getContentType());
            throw new IllegalArgumentException("Invalid file type. Only PDF, JPEG, and PNG are allowed.");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            log.debug("Creating upload directory at {}", uploadPath);
            Files.createDirectories(uploadPath);
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // e.g., 1_550e8400-e29b-41d4-a716-446655440000_passport.pdf.enc
        String targetFilename = userId + "_" + UUID.randomUUID() + "_" + originalFilename + ".enc";

        Path targetLocation = uploadPath.resolve(targetFilename);
        log.debug("Target file location resolved to: {}", targetLocation);

        byte[] encryptedBytes = fileEncryptionUtil.encrypt(file.getBytes());
        Files.write(targetLocation, encryptedBytes);
        log.info("File successfully encrypted and stored to disk for user ID: {}", userId);

        return targetFilename; // Just return filename or relative path
    }

    public byte[] loadFileAndDecrypt(String filename) throws Exception {
        Path filePath = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(filename).normalize();
        
        if (!Files.exists(filePath)) {
            log.error("Fail to decrypt, file not found: {}", filename);
            throw new RuntimeException("File not found " + filename);
        }

        byte[] encryptedFileBytes = Files.readAllBytes(filePath);
        return fileEncryptionUtil.decrypt(encryptedFileBytes);
    }
}
