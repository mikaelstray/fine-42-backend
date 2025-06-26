package com.mikael.project.backend.model.mappers;

import com.mikael.project.backend.model.dtos.household.HouseholdRequest;
import com.mikael.project.backend.model.dtos.household.HouseholdResponse;
import com.mikael.project.backend.model.dtos.user.LiteUserResponse;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HouseholdMapper {
  private final UserMapper userMapper;

  public Household toEntity(HouseholdRequest req, String inviteCode, User admin) {
    if (req == null || admin == null) return null;
    return new Household()
            .setName(req.name())
            .setInviteCode(inviteCode)
            .setAdminUser(admin);
  }

  public HouseholdResponse toDto(Household h) {
    LiteUserResponse adminDto = userMapper.toLiteDto(h.getAdminUser());
    Set<LiteUserResponse> members = h.getMembers().stream()
            .map(userMapper::toLiteDto)
            .collect(Collectors.toSet());


    return new HouseholdResponse(
            h.getId(),
            h.getName(),
            adminDto,
            h.getInviteCode(),
            members
    );
  }

  public List<HouseholdResponse> toDtoList(List<Household> list) {
    if (list == null) return Collections.emptyList();
    return list.stream()
            .map(this::toDto)
            .toList();
  }
}
