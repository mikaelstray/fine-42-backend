package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.household.HouseholdRequest;
import com.mikael.project.backend.model.dtos.household.HouseholdResponse;
import com.mikael.project.backend.model.dtos.household.JoinHouseholdRequest;
import com.mikael.project.backend.model.dtos.user.View;
import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.mappers.HouseholdMapper;
import com.mikael.project.backend.services.HouseholdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/households")
@RequiredArgsConstructor
@Validated
public class HouseholdController {

  private final HouseholdService householdService;
  private static final Logger logger = LogManager.getLogger(HouseholdController.class);
  private final HouseholdMapper householdMapper;


  @PostMapping("/create")
  public ResponseEntity<HouseholdResponse> create(
          @Valid @RequestBody HouseholdRequest request
  ) {
    HouseholdResponse response = householdService.createHousehold(request);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
  }

  @PostMapping("/join")
  public ResponseEntity<HouseholdResponse> join(
          @Valid @RequestBody JoinHouseholdRequest request
          ) {
    HouseholdResponse householdResponse = householdService.joinHousehold(request);
    return ResponseEntity.ok(householdResponse);
  }

  @DeleteMapping("/leave")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void leave(
          @RequestParam(value = "newAdminId", required = false) Long newAdminId
  ) {
    logger.info("leaving new admin {}", newAdminId);
    householdService.leaveHousehold(newAdminId);
  }

  @GetMapping("/me")
  public ResponseEntity<HouseholdResponse> getForCurrentUser() {
    Optional<Household> household = householdService.getHouseholdWithMembersForCurrentUser();

    return household
            .map(householdMapper::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());

  } //TODO: use responseentity.of

  @GetMapping("/members")
  public ResponseEntity<List<?>> getAllInHousehold(
          @RequestParam(defaultValue = "false") boolean excludeMe,
          @RequestParam(defaultValue = "full") View view
          ) {
    List<?> users = householdService.getAllInHousehold(excludeMe, view);
    return ResponseEntity.ok(users);
  }
}
