package com.mikael.project.backend.services;

import com.mikael.project.backend.config.SecurityUtil;
import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.AppEntityNotFoundException;
import com.mikael.project.backend.exception.customExceptions.EntityOperationException;
import com.mikael.project.backend.model.dtos.household.HouseholdRequest;
import com.mikael.project.backend.model.dtos.household.HouseholdResponse;
import com.mikael.project.backend.model.dtos.household.JoinHouseholdRequest;
import com.mikael.project.backend.model.dtos.user.LiteUserResponse;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.model.dtos.user.View;
import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.mappers.FineMapper;
import com.mikael.project.backend.model.mappers.HouseholdMapper;
import com.mikael.project.backend.model.mappers.UserMapper;
import com.mikael.project.backend.repo.HouseholdRepository;
import com.mikael.project.backend.repo.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HouseholdService {

  private final SecurityUtil securityUtil;
  private final HouseholdRepository householdRepository;
  private final HouseholdMapper householdMapper;
  private static final Logger logger = LogManager.getLogger(HouseholdService.class);
  private final UserMapper userMapper;
  private final UserRepository userRepository;

  @PersistenceContext
  private final EntityManager entityManager;

  @Transactional
  public HouseholdResponse createHousehold(HouseholdRequest request) {
    User currentUser = securityUtil.requireCurrentUser();

    Household household = householdMapper.toEntity(
            request,
            generateInvCode(),
            currentUser
    );

    Household saved = householdRepository.save(household);

    currentUser.setHousehold(saved);

    return householdMapper.toDto(saved);
  }

  @Transactional
  public HouseholdResponse joinHousehold(JoinHouseholdRequest request) {
    User currentUser = securityUtil.requireCurrentUser();

    Household household = householdRepository.findHouseholdByInviteCode(request.inviteCode())
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND));
    if (household.getMembers().contains(currentUser)) {
      throw new IllegalStateException("User is already member");
    }

    currentUser.setHousehold(household);
    household.getMembers().add(currentUser);

    return householdMapper.toDto(household); //TODO: map
  }

  @Transactional
  public void leaveHousehold(Long newAdminId) {
    User me = securityUtil.requireCurrentUser();
    Household hh = householdRepository.findById((me.getHousehold().getId()))
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND));

    if (hh.getMembers().size() == 1) {
      me.setHousehold(null);

      //flush to avoid fk problems
      entityManager.flush();

      //delete household directly without Hibernate state management
      householdRepository.deleteDirectly(hh.getId());
      return;
    }

    if (me.getId().equals(hh.getAdminUser().getId())) {
      changeHhAdmin(hh, newAdminId);
    }

    me.setHousehold(null);
  }

  @Transactional
  protected void changeHhAdmin(Household hh, Long userId) {
    User newAdmin = userRepository.findById(userId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.USER_NOT_FOUND));

    if (!hh.getMembers().contains(newAdmin)) {
      throw new AppEntityNotFoundException(CustomErrorMessage.NOT_IN_HOUSEHOLD);
    }

    hh.setAdminUser(newAdmin);
  }

  public Household getHouseholdWithMembers(Long userId) {
    Long householdId = userRepository.findById(userId)
            .map(User::getHousehold)
            .map(Household::getId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND));

    return householdRepository.findWithMembersById(householdId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.HOUSEHOLD_NOT_FOUND));
  }

  public Optional<Household> getHouseholdWithMembersForCurrentUser() {
    User me = securityUtil.requireCurrentUser();
    if (me.getHousehold() == null) {
      return Optional.empty();
    }
    return Optional.of(getHouseholdWithMembers(me.getId()));
  }

  public List<?> getAllInHousehold(boolean excludeMe, View view) {
    User currentUser = securityUtil.requireCurrentUser();

    List<User> rawUsers = excludeMe
            ? userRepository.findAllByHousehold_IdAndIdNot(currentUser.getHousehold().getId(), currentUser.getId())
            : userRepository.findAllByHousehold_Id(currentUser.getHousehold().getId());

    return switch (view) {
      case FULL -> userMapper.toDtoList(rawUsers);
      case LITE -> userMapper.toLiteDtoList(rawUsers);
    };
  }

  private String generateInvCode() {
    return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }
}
