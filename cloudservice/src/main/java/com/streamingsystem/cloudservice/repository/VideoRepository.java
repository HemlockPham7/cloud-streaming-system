package com.streamingsystem.cloudservice.repository;

import com.streamingsystem.cloudservice.dto.VideoStatus;
import com.streamingsystem.cloudservice.entity.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<VideoEntity, UUID> {

    Page<VideoEntity> findByStatus(VideoStatus status, Pageable pageable);
}
