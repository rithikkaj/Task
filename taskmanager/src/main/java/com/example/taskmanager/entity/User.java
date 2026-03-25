package com.example.taskmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;
    private String email;

    @jakarta.persistence.Column(columnDefinition = "VARCHAR(20)")
    private String role = "USER";
    
    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}