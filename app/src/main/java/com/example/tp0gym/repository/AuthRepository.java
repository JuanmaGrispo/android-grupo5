package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.LoginRequest;
import com.example.tp0gym.modelo.OtpRequest;
import com.example.tp0gym.modelo.OtpVerifyRequest;
import com.example.tp0gym.modelo.OtpResponse;
import com.example.tp0gym.modelo.User;

import retrofit2.Callback;

public class AuthRepository {

    private final AuthApiService authService;

    public AuthRepository() {
        authService = RetrofitClient.getAuthApiService();
    }

    public void startRegister(String email, Callback<OtpResponse> callback) {
        OtpRequest request = new OtpRequest(email);
        authService.startRegister(request).enqueue(callback);
    }

    public void verifyRegister(String email, String code, Callback<User> callback) {
        OtpVerifyRequest request = new OtpVerifyRequest(email, code);
        authService.verifyRegister(request).enqueue(callback);
    }

    public void startLogin(String email, Callback<OtpResponse> callback) {
        OtpRequest request = new OtpRequest(email);
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
