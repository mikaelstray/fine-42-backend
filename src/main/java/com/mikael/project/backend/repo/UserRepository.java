package com.mikael.project.backend.repo;

import com.mikael.project.backend.model.entity.Household;
import com.mikael.project.backend.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);

  List<User> findAllByHousehold_IdAndIdNot(Long householdId, Long id);

  List<User> findAllByHousehold_Id(Long householdId);
}
