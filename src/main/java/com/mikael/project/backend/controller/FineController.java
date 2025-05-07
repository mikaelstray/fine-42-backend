package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.fine.FineRequest;
import com.mikael.project.backend.model.dtos.fine.FineResponse;
import com.mikael.project.backend.services.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@Validated
public class FineController {
  FineService fineService;

  @PostMapping
  public ResponseEntity<FineResponse> create(
          @Valid @RequestBody FineRequest request
          ) {
    FineResponse response = fineService.createFine(request);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
  }

  @GetMapping
  public ResponseEntity<List<FineResponse>> getAllFines() {
    List<FineResponse> fines = fineService.getAllFines();
    return ResponseEntity.ok(fines);
  }

}
