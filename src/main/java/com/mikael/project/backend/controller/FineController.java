package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.fine.FineRequest;
import com.mikael.project.backend.model.dtos.fine.FineResponse;
import com.mikael.project.backend.model.dtos.fine.FineStatsResponse;
import com.mikael.project.backend.model.dtos.fine.ListType;
import com.mikael.project.backend.services.FineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
@Validated
public class FineController { //TODO: better endpoints
  private final FineService fineService;
  private static final Logger logger = LogManager.getLogger(FineController.class);


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
    List<FineResponse> myGivenFines = fineService.getMyGivenFines();

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
  public void deleteFine(
          @PathVariable Long fineId
  ) {
    fineService.deleteFine(fineId);
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
    List<FineResponse> fines = fineService.getAllFinesInHousehold();

    return ResponseEntity.ok(fines);
  }

}
