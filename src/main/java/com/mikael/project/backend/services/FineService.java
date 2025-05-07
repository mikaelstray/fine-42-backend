package com.mikael.project.backend.services;

import com.mikael.project.backend.config.SecurityUtil;
import com.mikael.project.backend.exception.CustomErrorMessage;
import com.mikael.project.backend.exception.customExceptions.AppEntityNotFoundException;
import com.mikael.project.backend.model.dtos.fine.FineRequest;
import com.mikael.project.backend.model.dtos.fine.FineResponse;
import com.mikael.project.backend.model.entity.Fine;
import com.mikael.project.backend.model.entity.user.User;
import com.mikael.project.backend.model.mappers.FineMapper;
import com.mikael.project.backend.repo.FineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FineService {
  private final UserService userService;
  private final FineMapper fineMapper;
  private final FineRepository fineRepository;
  SecurityUtil securityUtil;


  @Transactional
  public FineResponse createFine(FineRequest request) {
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

  public List<FineResponse> getAllFines() {
    return fineMapper.toDtoList(fineRepository.findAll());
  }

}
