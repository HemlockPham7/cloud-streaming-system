package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.entity.VideoRenditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRenditionRepository extends JpaRepository<VideoRenditionEntity, UUID> {
}
