package com.mikael.project.backend.controller;

import com.mikael.project.backend.model.dtos.user.LiteUserResponse;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.mappers.UserMapper;
import com.mikael.project.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;
  private static final Logger logger = LogManager.getLogger(UserController.class);

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAll() {
    List<UserResponse> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe() {
    Optional<User> userOptional = userService.getCurrentUser();

    return userOptional
            .map(userMapper::toDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
  }

}
