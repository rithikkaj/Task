package com.example.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskmanager.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}