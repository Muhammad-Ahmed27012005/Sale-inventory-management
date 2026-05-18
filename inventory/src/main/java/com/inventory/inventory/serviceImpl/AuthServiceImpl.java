package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.AuthRequest;
import com.inventory.inventory.dto.AuthResponse;
import com.inventory.inventory.entity.User;
import com.inventory.inventory.exception.ResourceNotFoundException;
import com.inventory.inventory.repository.UserRepository;
import com.inventory.inventory.service.AuthService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

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
}