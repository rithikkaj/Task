package com.example.taskmanager.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskStatsDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService service;

    @PostMapping("/create")
    public Task create(@RequestBody TaskRequestDTO dto) {
        return service.createTask(dto);
    }

    @PutMapping("/status")
    public Task updateStatus(@RequestParam Long taskId,
                             @RequestParam TaskStatus status) {
        return service.updateStatus(taskId, status, "user");
    }

    @GetMapping("/employee/{id}")
    public List<Task> getTasks(@PathVariable Long id) {
        return service.getTasksByEmployee(id);
    }

    @GetMapping("/stats/employee/{id}")
    public TaskStatsDTO getTaskStats(@PathVariable Long id) {
        return service.getTaskStatsByEmployee(id);
    }

    @DeleteMapping("/{taskId}")
    public String delete(@PathVariable Long taskId,
                         @RequestParam Long empId) {
        service.deleteTask(taskId, empId);
        return "Deleted";
    }
}