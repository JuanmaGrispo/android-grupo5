package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.LoginRequest;
import com.example.tp0gym.modelo.OtpRequest;
import com.example.tp0gym.modelo.OtpResponse;
import com.example.tp0gym.modelo.OtpVerifyRequest;
import com.example.tp0gym.modelo.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Call<OtpResponse> startRegister(@Body OtpRequest request);

    @POST("auth/register/verify")
    Call<User> verifyRegister(@Body OtpVerifyRequest request);

    @POST("auth/login")
    Call<OtpResponse> startLogin(@Body Map<String, String> request);





    @POST("auth/login/verify")
    Call<User> verifyLogin(@Body OtpVerifyRequest request);

    @POST("auth/login")
    Call<User> login(@Body LoginRequest request);
}
