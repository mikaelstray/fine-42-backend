package com.mikael.project.backend.model.dtos.household;

public record HouseholdLiteResponse(
        Long id,
        String name,
        Long adminId
) {}
