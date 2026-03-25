package com.example.taskmanager.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Entity;

public enum Role {
    USER,
    ADMIN
}
