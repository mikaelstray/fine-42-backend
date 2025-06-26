package com.mikael.project.backend.model.dtos.user;

/**
 * DTO returned after successful authentication or registration.
 */
public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {}