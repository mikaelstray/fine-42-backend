package com.mikael.project.backend.services;

import com.mikael.project.backend.config.SecurityUtil;
import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.AppEntityNotFoundException;
import com.mikael.project.backend.model.dtos.fine.*;
import com.mikael.project.backend.model.entity.Fine;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.mappers.FineMapper;
import com.mikael.project.backend.repo.FineRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FineService {
  private final UserService userService;
  private final FineMapper fineMapper;
  private final FineRepository fineRepository;
  private final SecurityUtil securityUtil;
  private static final Logger logger = LogManager.getLogger(FineService.class);


  @Transactional
  public FineResponse createFine(FineRequest request) {
    logger.info("adding fine in service");
    User giver = securityUtil.requireCurrentUser();
    User receiver = userService.findUserById(request.receiverId());

    Fine fine = fineMapper.toEntity(request)
            .setGiver(giver)
            .setReceiver(receiver);

    Fine saved = fineRepository.save(fine);
    log.info("Created Fine id={} issuer={} receiver={}",
            saved.getId(), giver.getUsername(), receiver.getUsername());

    return fineMapper.toDto(saved);
  }

  @Transactional
  public FineResponse editFine(FineRequest fineRequest, Long fineId) {
    logger.info("Editing fine with id {} in service", fineId);
    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.FINE_NOT_FOUND));
    securityUtil.isFinegiver(fine);

    User newReceiver = userService.findUserById(fineRequest.receiverId());

    fine
            .setName(fineRequest.name())
            .setAmount(fineRequest.amount())
            .setDescription(fineRequest.description())
            .setReceiver(newReceiver);

    Fine updatedFine = fineRepository.save(fine);
    return fineMapper.toDto(updatedFine);
  }

  @Transactional
  public void deleteFine(Long fineId) {
    fineRepository.deleteById(fineId);
  }

  public List<FineResponse> getAllFines() {
    return fineMapper.toDtoList(fineRepository.findAll());
  }

  public FineResponse getFineById(Long id) {
    Fine fine = fineRepository.findById(id)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.FINE_NOT_FOUND));

    return fineMapper.toDto(fine);
  }

  public List<FineResponse> getMyDtoFines() {
     User currentUser = securityUtil.requireCurrentUser();

     return fineMapper.toDtoList(fineRepository.findAllByReceiverAndStatus(currentUser, Status.ACTIVE));
  }

  public List<FineResponse> getMyFines() {
    User currentUser = securityUtil.requireCurrentUser();

    return fineMapper.toDtoList(fineRepository.findAllByReceiverAndStatus(currentUser, Status.ACTIVE));
  }

  public List<FineResponse> getMyGivenFines() {
    User currentUser = securityUtil.requireCurrentUser();

    return fineMapper.toDtoList(fineRepository.findAllByGiverAndStatus(currentUser, Status.ACTIVE));
  }

  public FineStatsResponse getStats(ListType type) {
    return switch (type) {
      case RECEIVED -> fetchReceivedStats();
      case GIVEN -> fetchGivenStats();
      default -> fetchAllStats();
    };
  }

  private FineStatsResponse fetchAllStats() {
    return calculateStats(getAllFinesInHousehold());
  }

  private FineStatsResponse fetchGivenStats() {
    return calculateStats(getMyGivenFines());
  }

  private FineStatsResponse fetchReceivedStats() {
    return calculateStats(getMyFines());
  }

  private static FineStatsResponse calculateStats(List<FineResponse> fines) {
    logger.info(fines);
    Integer count = fines.size();
    BigDecimal sum = fines.stream()
            .map(f -> BigDecimal.valueOf(f.amount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new FineStatsResponse(sum.longValue(), count);
  }

  public List<FineResponse> getAllFinesInHousehold() {
    User currentUser = securityUtil.requireCurrentUser();
    Long householdId = currentUser.getHousehold().getId();

    return fineMapper.toDtoList(fineRepository.findAllByGiver_Household_IdOrReceiver_Household_IdAndStatus(householdId, householdId, Status.ACTIVE));
  }
}
