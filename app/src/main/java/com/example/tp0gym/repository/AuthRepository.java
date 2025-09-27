package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.LoginRequest;
import com.example.tp0gym.modelo.OtpRequest;
import com.example.tp0gym.modelo.OtpVerifyRequest;
import com.example.tp0gym.modelo.OtpResponse;
import com.example.tp0gym.modelo.User;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Callback;

public class AuthRepository {

    private final AuthApiService authService;

    @Inject
    public AuthRepository(AuthApiService authService) {
        this.authService = authService;
    }

    // Para login OTP (usuarios existentes o registro)
    public void startLoginOtp(String email, String password, Callback<OtpResponse> callback) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        if (password != null && !password.isEmpty()) {
            request.put("password", password); // opcional para usuarios que no tienen password
        }
        request.put("mode", "otp"); // obligatorio para que el backend sepa que es OTP
        authService.startLogin(request).enqueue(callback);
    }

    public void verifyLogin(String email, String code, Callback<User> callback) {
        OtpVerifyRequest request = new OtpVerifyRequest(email, code);
        authService.verifyLogin(request).enqueue(callback);
    }

    public void login(String email, String password, Callback<User> callback) {
        LoginRequest request = new LoginRequest(email, password);
        authService.login(request).enqueue(callback);
    }
}
