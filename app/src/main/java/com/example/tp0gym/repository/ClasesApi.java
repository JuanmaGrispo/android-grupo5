package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.Clase;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ClasesApi {
    // Solo el endpoint relativo, la base URL se define en RetrofitClient
    @GET("classes")
    Call<List<Clase>> getClases(@Header("Authorization") String token);
}
