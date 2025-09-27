// com/example/tp0gym/repository/AuthApiService.java
package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.LoginRequest;
import com.example.tp0gym.modelo.OtpRequest;
import com.example.tp0gym.modelo.OtpResponse;
import com.example.tp0gym.modelo.OtpVerifyRequest;
import com.example.tp0gym.modelo.User;
import com.example.tp0gym.modelo.AttendanceDto; // ← agrega este import

import java.util.List;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;     // ← agrega
import retrofit2.http.Header;  // ← agrega
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

    // 👇 NUEVO: historial de asistencias
    // Endpoint correcto según documentación: /api/v1/attendance/me
    @GET("attendance/me")
    Call<List<AttendanceDto>> getMyAttendance(@Header("Authorization") String bearerToken);
}
