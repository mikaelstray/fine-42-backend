package com.mikael.project.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

  private final Cloudinary cloudinary;

  public String storeFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    try {
      String publicId = "fines/" + UUID.randomUUID().toString();

      Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
              "public_id", publicId
      ));

      return uploadResult.get("secure_url").toString();

    } catch (IOException e) {
      throw new RuntimeException("Could not store file. Please try again!", e);
    }
  }
}