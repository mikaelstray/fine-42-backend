package com.mikael.project.backend.model.dtos.user;

import com.mikael.project.backend.model.dtos.household.HouseholdLiteResponse;

import java.util.Set;

/**
 * Response DTO for returning User data.
 */
public record UserResponse(
        Long id,
        String username,
        Set<String> roles,
        HouseholdLiteResponse household
) {}
