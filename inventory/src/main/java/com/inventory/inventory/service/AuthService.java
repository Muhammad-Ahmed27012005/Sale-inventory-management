package com.inventory.inventory.service;


import com.inventory.inventory.dto.AuthRequest;
import com.inventory.inventory.dto.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthRequest request);
}