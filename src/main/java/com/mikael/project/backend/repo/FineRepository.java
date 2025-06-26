package com.mikael.project.backend.repo;

import com.mikael.project.backend.model.dtos.fine.FineResponse;
import com.mikael.project.backend.model.dtos.fine.Status;
import com.mikael.project.backend.model.entity.Fine;
import com.mikael.project.backend.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
  List<Fine> findAllByReceiverAndStatus(User receiver, Status status);

  List<Fine> findAllByGiverAndStatus(User giver, Status status);

  List<Fine> findAllByGiver_Household_IdOrReceiver_Household_IdAndStatus(Long giverHouseholdId, Long receiverHouseholdId, Status status);
}
