package com.mikael.project.backend.config;

import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.UnauthorizedOperationException;
import com.mikael.project.backend.model.entity.Fine;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.repo.UserRepository;
import com.mikael.project.backend.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

  private final UserRepository userRepository;
  private static final Logger logger = LogManager.getLogger(SecurityUtil.class);


  /** Hent autentisert bruker eller tom hvis anonym. */
  public Optional<User> getCurrentUserOptional() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    logger.info(auth);
    if (auth == null ||
            !auth.isAuthenticated() ||
            auth instanceof AnonymousAuthenticationToken) {
      return Optional.empty();
    }
    return userRepository.findByUsername(auth.getName());
  }

  //TODO: change to return id, not repo call
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

  public boolean isFinegiver(Fine fine) { //TODO: remove
    return requireCurrentUser().getId().equals(fine.getGiver().getId());
  }
}
