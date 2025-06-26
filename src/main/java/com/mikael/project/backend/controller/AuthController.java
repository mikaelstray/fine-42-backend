package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.user.AuthResponse;
import com.mikael.project.backend.model.dtos.user.LoginRequest;
import com.mikael.project.backend.model.dtos.user.RegisterRequest;
import com.mikael.project.backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Validated
public class AuthController {

  private static final Logger logger = LogManager.getLogger(AuthController.class);
  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(
          @RequestBody @Valid RegisterRequest request
  ) {
    logger.info("Registering user in controller");
    AuthResponse authResponse = authService.register(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
          @RequestBody @Valid LoginRequest request
          ) {
    AuthResponse authResponse = authService.login(request);

    return ResponseEntity.ok(authResponse);
  }

}
