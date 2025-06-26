package com.mikael.project.backend.repo;

import com.mikael.project.backend.model.entity.Household;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
  Optional<Household> findHouseholdByInviteCode(String inviteCode);

  @EntityGraph(attributePaths = {"members"})
  Optional<Household> findWithMembersById(Long id);

  @Modifying
  @Query("DELETE FROM Household h WHERE h.id = :id")
  void deleteDirectly(@Param("id") Long id);

}
