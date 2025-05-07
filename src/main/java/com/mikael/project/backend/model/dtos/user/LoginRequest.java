package com.mikael.project.backend.model.dtos.user;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login.
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {}