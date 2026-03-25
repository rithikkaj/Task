package com.example.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStatsDTO {
    private Long totalTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long todoTasks;
    private Double completionPercentage;
}
