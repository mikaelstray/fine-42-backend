package com.mikael.project.backend.repo;

import com.mikael.project.backend.model.dtos.fine.Status;
import com.mikael.project.backend.model.entity.fine.Fine;
import com.mikael.project.backend.model.entity.fine.FineStatus;
import com.mikael.project.backend.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
  List<Fine> findAllByReceiver_Id(Long receiverId);

  List<Fine> findAllByGiver_Id(Long giverId);
  
  List<Fine> findAllByGiver_IdAndStatus_(Long giverId, FineStatus status);
  
  List<Fine> findAllByReceiver_IdAndStatus(Long receiverId, FineStatus status);
  
  List<Fine> findAllByGiver_Household_IdOrReceiver_Household_Id(Long giverHouseholdId, Long receiverHouseholdId);

  void deleteAllByHousehold_Id(Long householdId);

  @Transactional
  @Modifying
  @Query("UPDATE Fine f SET f.status = 'PAID' WHERE f.status = 'UNPAID'")
  void markAllAsPaid();
}
