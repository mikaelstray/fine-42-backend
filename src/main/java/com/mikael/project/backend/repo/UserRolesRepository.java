package com.mikael.project.backend.repo;

import com.mikael.project.backend.model.entity.user.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRolesRepository extends JpaRepository<UserRoles, Long> {

}
