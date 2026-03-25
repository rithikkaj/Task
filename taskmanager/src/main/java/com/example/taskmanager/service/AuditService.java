package com.example.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import com.example.taskmanager.entity.AuditLog;
import com.example.taskmanager.repository.AuditLogRepository;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repo;

    public void log(String action, String entity, Long id, String user) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityName(entity);
        log.setEntityId(id);
        log.setPerformedBy(user);
        log.setTimestamp(LocalDateTime.now());
        repo.save(log);
    }
}