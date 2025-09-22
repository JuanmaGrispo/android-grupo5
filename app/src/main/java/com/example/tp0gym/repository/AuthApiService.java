package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.LoginRequest;
import com.example.tp0gym.modelo.OTPRequest;
import com.example.tp0gym.modelo.OTPResponse;
import com.example.tp0gym.modelo.OTPVerifyRequest;
import com.example.tp0gym.modelo.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<OTPResponse> startRegister(@Body OTPRequest request);

    @POST("auth/register/verify")
    Call<User> verifyRegister(@Body OTPVerifyRequest request);

    @POST("auth/login")
    Call<OTPResponse> startLogin(@Body OTPRequest request);

    @POST("auth/login/verify")
    Call<User> verifyLogin(@Body OTPVerifyRequest request);

    @POST("auth/login")
    Call<User> login(@Body LoginRequest request);
}
