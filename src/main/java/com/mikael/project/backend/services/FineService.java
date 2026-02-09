package com.mikael.project.backend.services;

import com.mikael.project.backend.config.SecurityUtil;
import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.AppEntityNotFoundException;
import com.mikael.project.backend.exception.customExceptions.EntityOperationException;
import com.mikael.project.backend.model.dtos.fine.*;
import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.entity.fine.Fine;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.mappers.FineMapper;
import com.mikael.project.backend.repo.FineRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
  private final FileStorageService fileStorageService;

  @Transactional
  public FineResponse createFine(FineRequest request, MultipartFile imageFile) {
    logger.info("adding fine in service");
    User giver = securityUtil.requireCurrentUser();
    User receiver = userService.findUserById(request.receiverId());
    Household household = giver.getHousehold();

    Fine fine = fineMapper.toEntity(request)
            .setGiver(giver)
            .setReceiver(receiver)
            .setHousehold(household);

    if (imageFile != null && !imageFile.isEmpty()) {
      try {
        String imageUrl = fileStorageService.storeFile(imageFile);
        fine.setImageUrl(imageUrl);
      } catch (Exception e) {
        throw new RuntimeException(e); //TODO fix better
      }
    }

    Fine saved = fineRepository.save(fine);
    log.info("Created Fine id={} issuer={} receiver={}",
            saved.getId(), giver.getUsername(), receiver.getUsername());

    return fineMapper.toDto(saved); //TODO: notifications and pagination and mantine noti isntead of toast
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
  public FineResponse deleteFine(Long fineId) {
    Fine fine = fineRepository.findById(fineId)
            .orElseThrow(() -> new AppEntityNotFoundException(CustomErrorMessage.FINE_NOT_FOUND));

    fineRepository.deleteById(fine.getId());

    return fineMapper.toDto(fine);
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

     return fineMapper.toDtoList(fineRepository.findAllByReceiver_Id(currentUser.getId()));
  }

  public List<Fine> getMyFines() {
    User currentUser = securityUtil.requireCurrentUser();

    return fineRepository.findAllByReceiver_Id(currentUser.getId());
  }

  public List<Fine> getMyGivenFines() {
    User currentUser = securityUtil.requireCurrentUser();

    return fineRepository.findAllByGiver_Id(currentUser.getId());
  }

  public FineStatsResponse getStats(ListType type) {
    return switch (type) {
      case RECEIVED -> fetchReceivedStats();
      case GIVEN -> fetchGivenStats();
      default -> fetchAllStats();
    };
  }

  public UserFineStatsResponse getFineStatsByUser(Long userId) {
    FineStatsResponse givenStats = calculateStats(fineRepository.findAllByGiver_Id(userId)); //TODO: choose between paid and all
    FineStatsResponse receivedStats = calculateStats(fineRepository.findAllByReceiver_Id(userId));

    return new UserFineStatsResponse(givenStats, receivedStats);
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

  private static FineStatsResponse calculateStats(List<Fine> fines) {
    logger.info(fines);
    Integer count = fines.size();
    BigDecimal sum = fines.stream()
            .map(f -> BigDecimal.valueOf(f.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    return new FineStatsResponse(sum.longValue(), count);
  }

  public List<Fine> getAllFinesInHousehold() {
    User currentUser = securityUtil.requireCurrentUser();
    Long householdId = currentUser.getHousehold().getId(); //TODO: not in hh exception

    return fineRepository.findAllByGiver_Household_IdOrReceiver_Household_Id(householdId, householdId);
  }

  @Transactional
  public void deleteAllFinesInHousehold() {
    Long householdId = securityUtil.requireCurrentUser().getHousehold().getId(); //TODO: not in hh exception
    fineRepository.deleteAllByHousehold_Id(householdId);
  }

  @Transactional
  public void approveAllFines() {
    fineRepository.markAllAsPaid();
  }
}
