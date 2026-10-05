package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.entity.ProcessS3MigrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessS3MigrationRepository extends JpaRepository<ProcessS3MigrationEntity, Integer> {
}
