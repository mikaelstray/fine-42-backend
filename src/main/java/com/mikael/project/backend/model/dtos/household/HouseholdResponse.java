package com.mikael.project.backend.model.dtos.household;

import com.mikael.project.backend.model.dtos.user.LiteUserResponse;

import java.util.Set;

public record HouseholdResponse(
        Long id,
        String name,
        LiteUserResponse admin,
        String inviteCode,
        Set<LiteUserResponse> members
) {}
