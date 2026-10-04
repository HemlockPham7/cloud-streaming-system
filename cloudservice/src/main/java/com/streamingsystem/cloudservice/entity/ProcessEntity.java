package com.streamingsystem.cloudservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Blob;

@Entity
@Table(name = "process")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String description;

    @Lob
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "image")
    private Blob image;

    @Column(name = "status", length = 50)
    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "process_id", insertable = false, updatable = false)
    private ProcessS3MigrationEntity s3Migration;
}