package com.neperix.osiris.security.jwt;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
