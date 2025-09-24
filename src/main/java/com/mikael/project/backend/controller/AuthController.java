package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.user.AuthResponse;
import com.mikael.project.backend.model.dtos.user.LoginRequest;
import com.mikael.project.backend.model.dtos.user.RegisterRequest;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

  private static final String AUTH_COOKIE_NAME = "auth-token";

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(
          @RequestBody @Valid RegisterRequest request
  ) {
    logger.info("Registering user in controller");
    AuthResponse authResponse = authService.register(request);

    ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, authResponse.token())
            .httpOnly(true)
            .secure(false) //TODO: fix https
            .path("/")
            .maxAge(60 * 60 * 24)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(authResponse.user());
  }

  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(
          @RequestBody @Valid LoginRequest request
          ) {
    AuthResponse authResponse = authService.login(request);

    ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, authResponse.token())
            .httpOnly(true)
            .secure(false) //fix https
            .path("/")
            .maxAge(60 * 60 * 24)
            .sameSite("Strict")
            .build();

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(authResponse.user());
  }

}
