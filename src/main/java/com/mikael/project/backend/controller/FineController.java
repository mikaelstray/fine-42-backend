package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.fine.*;
import com.mikael.project.backend.model.entity.fine.Fine;
import com.mikael.project.backend.model.mappers.FineMapper;
import com.mikael.project.backend.services.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@Validated
public class FineController { //TODO: better endpoints
  private final FineService fineService;
  private static final Logger logger = LogManager.getLogger(FineController.class);
  private final FineMapper mapper;


  @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
  public ResponseEntity<FineResponse> create(
          @RequestPart("fineRequest") FineRequest request,
          @RequestPart(value = "image", required = false) MultipartFile imageFile) {
    FineResponse response = fineService.createFine(request, imageFile);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
  }

  @GetMapping
  public ResponseEntity<List<FineResponse>> getAllFines() {
    List<FineResponse> fines = fineService.getAllFines();
    return ResponseEntity.ok(fines);
  }

  @GetMapping("/{fineId}")
  public ResponseEntity<FineResponse> getFineById(
          @PathVariable Long fineId
  ) {
    FineResponse fineResponse = fineService.getFineById(fineId);

    return ResponseEntity.ok(fineResponse);
  }

  @GetMapping("/me/received")
  public ResponseEntity<List<FineResponse>> getMyFines() {
    List<FineResponse> myFines = fineService.getMyDtoFines();

    return ResponseEntity.ok(myFines);
  }

  @GetMapping("/me/given")
  public ResponseEntity<List<FineResponse>> getMyGivenFines() {
    List<FineResponse> myGivenFines = mapper.toDtoList(fineService.getMyGivenFines());

    return ResponseEntity.ok(myGivenFines);
  }

  @PatchMapping("/edit/{fineId}") //TODO: remove edit in url
  public ResponseEntity<FineResponse> update(
          @PathVariable Long fineId,
          @RequestBody @Valid FineRequest fineRequest
  ) {
    logger.info("updating fine with fineId {} and {}{}", fineId,fineRequest.name(),fineRequest.amount());
    FineResponse updatedFine = fineService.editFine(fineRequest, fineId);
    return ResponseEntity.ok(updatedFine);
  }

  @DeleteMapping("/{fineId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<FineResponse> deleteFine(
          @PathVariable Long fineId
  ) {
    FineResponse fineResponse = fineService.deleteFine(fineId);

    return ResponseEntity.ok(fineResponse);
  }

  @GetMapping("/stats")
  public ResponseEntity<FineStatsResponse> getStats(
          @RequestParam(name = "type", defaultValue = "ALL") ListType listType
          ) {
    FineStatsResponse fineStatsResponse = fineService.getStats(listType);
    return ResponseEntity.ok(fineStatsResponse);
  }

  @GetMapping("/household")
  public ResponseEntity<List<FineResponse>> getAllInMyHousehold() {
    List<FineResponse> fines = mapper.toDtoList(fineService.getAllFinesInHousehold());

    return ResponseEntity.ok(fines);
  }

  @DeleteMapping("/household")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteAllFinesInHousehold() {
    fineService.deleteAllFinesInHousehold();
  }

  @PutMapping("/approve-all") //TODO: secure with hh admin
  public ResponseEntity<Void> approveAllFines() {
    fineService.approveAllFines();
    return ResponseEntity.ok().build();
  }

  @GetMapping("/stats/{userId}")
  public ResponseEntity<UserFineStatsResponse> getFinesStatsByUserId(
          @PathVariable Long userId
  ) {
    UserFineStatsResponse userFineStats = fineService.getFineStatsByUser(userId);
    return ResponseEntity.ok(userFineStats);
  }
}
