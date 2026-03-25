package com.example.taskmanager.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.dto.SignUpDTO;
import com.example.taskmanager.entity.Employee;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.EmployeeRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            String username = userDetails.getUsername();
            String role = userDetails.getAuthorities().iterator().next().getAuthority().substring(5); // remove ROLE_
            String token = jwtUtil.generateToken(username, role);
            
            // Get user from database to get ID and employee info
            User user = userRepository.findByUsername(username).orElseThrow();
            
            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(token);
            response.setRole(role);
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            if (user.getEmployee() != null) {
                response.setEmployeeId(user.getEmployee().getId());
            }
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"TaskManager\", error=\"invalid_credentials\", error_description=\"Authentication failed. Invalid username or password.\"")
                .build();
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"TaskManager\", error=\"invalid_request\", error_description=\"" + e.getMessage() + "\"")
                .build();
        }
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponseDTO> signup(@RequestBody SignUpDTO request) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
            }

            // Create new employee
            Employee employee = new Employee();
            employee.setName(request.getUsername());
            employee.setEmail(request.getEmail());
            Employee savedEmployee = employeeRepository.save(employee);

            // Create new user
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(request.getPassword());
            newUser.setEmail(request.getEmail());
            newUser.setRole("USER");
            newUser.setEmployee(savedEmployee);
            
            User savedUser = userRepository.save(newUser);

            // Generate token for the newly created user
            String token = jwtUtil.generateToken(request.getUsername(), "USER");
            AuthResponseDTO response = new AuthResponseDTO();
            response.setToken(token);
            response.setRole("USER");
            response.setUserId(savedUser.getId());
            response.setEmployeeId(savedEmployee.getId());
            response.setUsername(savedUser.getUsername());
            response.setEmail(savedUser.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }
}

