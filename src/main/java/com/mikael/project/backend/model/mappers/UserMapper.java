package com.mikael.project.backend.model.mappers;

import com.mikael.project.backend.model.dtos.fine.FineResponse;
import com.mikael.project.backend.model.dtos.user.*;
import com.mikael.project.backend.model.entity.Fine;
import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.entity.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  public User toEntity(RegisterRequest request) {
    if (request == null) return null;
    User user = new User();
    user.setUsername(request.username());
    user.setPassword(request.password());
    return user;
  }

  public UserResponse toDto(User user) {
    Long householdId = Optional.ofNullable(user.getHousehold())
            .map(Household::getId)
            .orElse(null);
    Set<String> roles = user.getRoles() == null
            ? Set.of()
            : user.getRoles().stream()
            .map(ur -> ur.getRole().name())
            .collect(Collectors.toSet());
    return new UserResponse(
            user.getId(),
            user.getUsername(),
            roles,
            householdId
    );
  }

  public LiteUserResponse toLiteDto(User user) {
    return new LiteUserResponse(
            user.getId(),
            user.getUsername()
    );
  }

  public List<UserResponse> toDtoList(List<User> users) {
    if (users == null) return Collections.emptyList();

    return users.stream()
            .map(this::toDto)
            .toList();
  }

  public List<LiteUserResponse> toLiteDtoList(List<User> users) {
    if (users == null) return Collections.emptyList();

    return users.stream()
            .map(this::toLiteDto)
            .toList();
  }
}