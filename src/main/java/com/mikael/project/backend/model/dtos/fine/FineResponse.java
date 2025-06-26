package com.mikael.project.backend.model.dtos.fine;

import com.mikael.project.backend.model.dtos.user.UserResponse;

import java.time.LocalDateTime;

/**
 * Response DTO for returning Fine data.
 */
public record FineResponse(
        Long id,
        String name,
        Integer amount,
        String description,
        LocalDateTime issuedAt,
        UserResponse receiver,
        UserResponse giver
) {}
