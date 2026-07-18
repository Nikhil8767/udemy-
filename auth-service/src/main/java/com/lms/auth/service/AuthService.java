package com.lms.auth.service;

import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RegisterRequest;
import com.lms.auth.dto.response.JwtResponse;
import com.lms.auth.entity.UserCredential;

public interface AuthService {
    void register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    UserCredential getAuthenticatedUser();
}
