package com.mikael.project.backend.model.mappers;

import com.mikael.project.backend.model.dtos.fine.*;
import com.mikael.project.backend.model.dtos.user.UserResponse;
import com.mikael.project.backend.model.entity.Fine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FineMapper {

  private final UserMapper userMapper;

  public Fine toEntity(FineRequest request) {
    if (request == null) return null;

    return new Fine()
            .setName(request.name())
            .setAmount(request.amount())
            .setDescription(request.description());
  }

  public FineResponse toDto(Fine fine) {
    if (fine == null) return null;
    UserResponse receiverDto = userMapper.toDto(fine.getReceiver());
    UserResponse giverDto = userMapper.toDto(fine.getGiver());

    return new FineResponse(
            fine.getId(),
            fine.getName(),
            fine.getAmount(),
            fine.getDescription(),
            fine.getIssuedAt(),
            receiverDto,
            giverDto
    );
  }

  public List<FineResponse> toDtoList(List<Fine> fines) {
    if (fines == null) return Collections.emptyList();

    return fines.stream()
            .map(this::toDto)
            .toList();
  }
}