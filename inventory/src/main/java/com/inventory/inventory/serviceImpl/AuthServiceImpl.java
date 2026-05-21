package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.AuthRequest;
import com.inventory.inventory.dto.AuthResponse;
import com.inventory.inventory.dto.RegisterRequest;
import com.inventory.inventory.entity.User;
import com.inventory.inventory.exception.BadRequestException;
import com.inventory.inventory.exception.ResourceNotFoundException;
import com.inventory.inventory.repository.UserRepository;
import com.inventory.inventory.service.AuthService;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String rawToken = request.getUsername() + ":" + request.getPassword();
        String token = "Basic " + Base64.getEncoder().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
        return new AuthResponse(user.getUsername(), user.getRole(), token, "Login successful");
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BadRequestException("Username is already taken");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build());

        String rawToken = username + ":" + request.getPassword();
        String encodedToken = Base64.getEncoder().encodeToString(rawToken.getBytes(StandardCharsets.UTF_8));
        return new AuthResponse(user.getUsername(), user.getRole(), "Basic " + encodedToken,
                "Account created successfully");
    }
}