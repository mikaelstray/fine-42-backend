package com.mikael.project.backend.services;

import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.AppEntityNotFoundException;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  public User findUserById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND));
  }

}
