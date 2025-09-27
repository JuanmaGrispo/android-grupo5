package com.example.tp0gym.repository.api;

import retrofit2.http.GET;
import retrofit2.http.Header;
import com.example.tp0gym.modelo.Branch;

public interface BranchesApi {
    @GET("branches")
    Call<List<Branch>> getBranches(@Header("Authorization") String token);
}
