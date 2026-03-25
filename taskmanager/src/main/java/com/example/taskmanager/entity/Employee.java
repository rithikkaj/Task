package com.example.taskmanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Employee {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;

    @OneToOne(mappedBy = "employee")
    private User user;
}