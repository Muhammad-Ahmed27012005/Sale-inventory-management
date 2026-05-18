package com.inventory.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String username;
    private String role;
    private String token;
    private String message;
}