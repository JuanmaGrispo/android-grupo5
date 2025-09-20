package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.Clase;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ClaseApiService {
    @GET("api/v1/classes")
    Call<List<Clase>> getClases();
}
