package com.mikael.project.backend.services;

import com.mikael.project.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) {
    log.info("Loading user '{}'", username);
    return userRepository.findByUsername(username)
            .orElseThrow(() -> {
              log.warn("User '{}' not found", username);
              return new UsernameNotFoundException("User not found: " + username);
            });
  }
}
