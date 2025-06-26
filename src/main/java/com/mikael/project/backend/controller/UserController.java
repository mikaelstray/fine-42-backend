package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.user.LiteUserResponse;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private static final Logger logger = LogManager.getLogger(UserController.class);

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAll() {
    List<UserResponse> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe() {
    logger.info("getting current");
    UserResponse user = userService.getCurrentUser();
    logger.info("got current user: {} ", user );
    return ResponseEntity.ok(user);
  }

}
