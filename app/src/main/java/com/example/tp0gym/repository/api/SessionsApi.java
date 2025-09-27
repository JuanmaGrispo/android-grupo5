package com.example.tp0gym.repository.api;

import com.example.tp0gym.modelo.SessionsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface SessionsApi {
    
    @GET("classes/sessions")
    Call<SessionsResponse> getSessions(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
    
    @GET("classes/sessions")
    Call<SessionsResponse> getSessionsWithFilters(
            @Header("Authorization") String token,
            @Query("from") String from,
            @Query("to") String to,
            @Query("branchId") String branchId,
            @Query("classRefId") String classRefId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
