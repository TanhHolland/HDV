package com.example.gateway.security;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class RouteValidator {
    private final Map<Pattern, String> protectedRoutes = new HashMap<>();

    public RouteValidator() {
        addProtectedRoute("/api/intentions/place", "Customer");

        addProtectedRoute("/api/position/update", "Driver");
        addProtectedRoute("/api/intentions/confirm", "Driver");

        addProtectedRoute("/api/order/.*", "ANY");
        
        addProtectedRoute("/users/.*", "ANY");
        addProtectedRoute("/api/position/.*", "ANY");
    }

    private void addProtectedRoute(String path, String role) {
        protectedRoutes.put(Pattern.compile(path), role);
    }

    public String getRequiredRole(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        
        for (Map.Entry<Pattern, String> entry : protectedRoutes.entrySet()) {
            if (entry.getKey().matcher(path).matches()) {
                return entry.getValue();
            }
        }
        return null;
    }
}