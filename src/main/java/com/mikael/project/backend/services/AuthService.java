package com.mikael.project.backend.services;

import com.mikael.project.backend.config.JWTUtil;
import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.EntityAlreadyExistsException;
import com.mikael.project.backend.model.dtos.user.AuthResponse;
import com.mikael.project.backend.model.dtos.user.RegisterRequest;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.model.entity.user.Role;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.entity.user.UserRoles;
import com.mikael.project.backend.model.mappers.UserMapper;
import com.mikael.project.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JWTUtil jwtUtil;


  @Transactional
  public AuthResponse register(RegisterRequest request) {
    checkExistsByUsername(request.username());

    User user = userMapper.toEntity(request);
    user.setPassword(passwordEncoder.encode(request.password()));

    UserRoles role = new UserRoles()
            .setUser(user)
            .setRole(Role.ROLE_USER);
    user.setRoles(Set.of(role));

    User saved = userRepository.save(user);
    log.info("Registered new user '{}'", saved.getUsername());

    String token = jwtUtil.generateToken(saved);

    UserResponse userDto = userMapper.toDto(saved);
    return new AuthResponse(token, "Bearer", userDto);
  }

  private void checkExistsByUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new EntityAlreadyExistsException(CustomErrorMessage.USERNAME_ALREADY_EXISTS);
    }
  }

}
