package com.mikael.project.backend.config;

import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.UnauthorizedOperationException;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

  private final UserRepository userRepository;

  /** Hent autentisert bruker eller tom hvis anonym. */
  public Optional<User> getCurrentUserOptional() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null ||
            !auth.isAuthenticated() ||
            auth instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    return userRepository.findByUsername(auth.getName());
  }

  /** Hent autentisert bruker eller kast Unauthorized. */
  public User requireCurrentUser() {
    return getCurrentUserOptional()
            .orElseThrow(() -> new UnauthorizedOperationException(
                    CustomErrorMessage.UNAUTHORIZED_OPERATION
            ));
  }

  /** Sjekk om nåværende bruker har gitt rolle. */
  public boolean hasRole(String authority) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return auth != null &&
            auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(authority));
  }

  /** Sjekk og kast hvis ikke ADMIN. */
  public void requireAdmin() {
    if (!hasRole("ROLE_ADMIN")) {
      throw new UnauthorizedOperationException(
              CustomErrorMessage.UNAUTHORIZED_OPERATION
      );
    }
  }
}
