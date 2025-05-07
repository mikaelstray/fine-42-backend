package com.mikael.project.backend.model.dtos.user;

import java.util.Set;
import java.time.LocalDateTime;

/**
 * Response DTO for returning User data.
 */
public record UserResponse(
        Long id,
        String username,
        Set<String> roles
) {}
