package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class AuditLog {

    @Id
    @GeneratedValue
    private Long id;

    private String action;
    private String entityName;
    private Long entityId;
    private String performedBy;
    private LocalDateTime timestamp;
}