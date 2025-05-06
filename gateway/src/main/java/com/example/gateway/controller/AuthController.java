package com.example.gateway.controller;

import com.example.gateway.model.AuthRequest;
import com.example.gateway.model.AuthResponse;
import com.example.gateway.model.User;
import com.example.gateway.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    private final Map<String, User> users = new HashMap<>();

    public AuthController() {
        users.put("customer1", new User("customer1", "password", "Customer"));
        users.put("customer2", new User("customer2", "password", "Customer"));
        users.put("driver1", new User("driver1", "password", "Driver"));
        users.put("driver2", new User("driver2", "password", "Driver"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = users.get(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            return ResponseEntity.ok(new AuthResponse(token, user.getRole()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        if (users.containsKey(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (!user.getRole().equals("Customer") && !user.getRole().equals("Driver")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthResponse(null, "Invalid role. Must be Customer or Driver"));
        }
        
        users.put(user.getUsername(), user);
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getRole()));
    }
}