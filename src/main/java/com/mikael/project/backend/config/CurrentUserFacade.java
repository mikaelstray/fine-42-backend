package com.mikael.project.backend.config;

import com.mikael.project.backend.exception.customExceptions.UnauthorizedOperationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;
/*
@Component
public class CurrentUserFacade {
  public UUID id() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !a.isAuthenticated() ||
            a instanceof AnonymousAuthenticationToken) {
      throw new UnauthorizedOperationException();
    }
    return ((CustomUserPrincipal) a.getPrincipal()).getId();
  }
} */