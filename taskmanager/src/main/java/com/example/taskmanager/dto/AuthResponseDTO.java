package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String role;
    private Long userId;
    private Long employeeId;
    private String username;
    private String email;
}
