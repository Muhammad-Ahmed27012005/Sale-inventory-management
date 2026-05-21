package com.inventory.inventory.service;


import com.inventory.inventory.dto.AuthRequest;
import com.inventory.inventory.dto.AuthResponse;
import com.inventory.inventory.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(AuthRequest request);

     AuthResponse register(RegisterRequest request);
}