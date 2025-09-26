package com.mikael.project.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

  private final Path fileStorageLocation;

  // Hent opplastingsmappe fra application.properties
  public FileStorageService(@Value("${file.upload-dir:uploads/images}") String uploadDir) {
    this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  public String storeFile(MultipartFile file) {
    // Normaliser filnavn
    String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

    try {
      if (originalFileName.contains("..")) {
        throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
      }

      // Lag et unikt filnavn for å unngå kollisjoner
      String fileExtension = "";
      try {
        fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
      } catch (Exception e) {
        // Håndter filer uten extension
      }
      String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

      // Kopier filen til mål-lokasjonen
      Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return uniqueFileName; // Returner kun filnavnet
    } catch (IOException ex) {
      throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
    }
  }
}