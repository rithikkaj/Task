package com.example.taskmanager.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskStatsDTO;
import com.example.taskmanager.entity.Employee;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.repository.EmployeeRepository;
import com.example.taskmanager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final EmployeeRepository empRepo;
    private final AuditService auditService;

    public Task createTask(TaskRequestDTO dto) {

        if(dto.getDueDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Due date cannot be past");
        }

        Employee assigned = empRepo.findById(dto.getAssignedToId()).orElseThrow();
        Employee creator = empRepo.findById(dto.getCreatedById()).orElseThrow();

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setAssignedTo(assigned);
        task.setCreatedBy(creator);
        task.setStatus(TaskStatus.TODO);
        task.setDueDate(dto.getDueDate());

        Task saved = taskRepo.save(task);
        auditService.log("CREATE_TASK", "Task", saved.getId(), creator.getName());

        return saved;
    }

    public Task updateStatus(Long taskId, TaskStatus status, String user) {
        Task task = taskRepo.findById(taskId).orElseThrow();
        task.setStatus(status);
        Task updated = taskRepo.save(task);

        auditService.log("UPDATE_STATUS", "Task", taskId, user);
        return updated;
    }

    public List<Task> getTasksByEmployee(Long empId) {
        return taskRepo.findByAssignedToId(empId);
    }

    public TaskStatsDTO getTaskStatsByEmployee(Long empId) {
        List<Task> tasks = getTasksByEmployee(empId);
        
        long totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long inProgressTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long todoTasks = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        
        double completionPercentage = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0.0;
        
        return new TaskStatsDTO(totalTasks, completedTasks, inProgressTasks, todoTasks, completionPercentage);
    }

    public void deleteTask(Long taskId, Long empId) {
        Task task = taskRepo.findById(taskId).orElseThrow();

        if(!task.getCreatedBy().getId().equals(empId)) {
            throw new RuntimeException("Only creator can delete");
        }

        taskRepo.delete(task);
        auditService.log("DELETE_TASK", "Task", taskId, "user");
    }
}