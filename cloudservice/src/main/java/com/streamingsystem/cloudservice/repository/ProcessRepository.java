package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.entity.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<ProcessEntity, Integer> {
}
