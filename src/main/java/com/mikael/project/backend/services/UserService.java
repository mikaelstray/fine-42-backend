package com.mikael.project.backend.services;

import com.mikael.project.backend.config.SecurityUtil;
import com.mikael.project.backend.controller.FineController;
import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.AppEntityNotFoundException;
import com.mikael.project.backend.model.dtos.user.LiteUserResponse;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.mappers.UserMapper;
import com.mikael.project.backend.repo.HouseholdRepository;
import com.mikael.project.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SecurityUtil securityUtil;
  private static final Logger logger = LogManager.getLogger(UserService.class);

  public List<UserResponse> getAllUsers() {
    return userMapper.toDtoList(userRepository.findAll());
  }

  public UserResponse getCurrentUser() {
    return userMapper.toDto(securityUtil.requireCurrentUser());
  }

  public User findUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND));
  }

  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND));
  }

}
