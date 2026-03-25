package com.example.taskmanager.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TaskRequestDTO {
    private String title;
    private String description;
    private Long assignedToId;
    private Long createdById;
    private LocalDate dueDate;
}