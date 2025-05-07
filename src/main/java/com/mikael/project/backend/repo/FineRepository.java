package com.mikael.project.backend.repo;

import com.mikael.project.backend.model.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, Long> {

}
