package com.example.tp0gym.repository.api;

import com.example.tp0gym.modelo.BranchDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface BranchesApi {
    
    @GET("branches")
    Call<List<BranchDto>> getBranches(@Header("Authorization") String token);
}
