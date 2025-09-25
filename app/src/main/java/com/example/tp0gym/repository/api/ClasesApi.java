package com.example.tp0gym.repository.api;

import com.example.tp0gym.modelo.Clase;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ClasesApi {

    // Método para obtener todas las clases sin filtros
    @GET("classes")
    Call<List<Clase>> getClases(@Header("Authorization") String token);

    // Método para obtener clases con filtros opcionales
    @GET("classes")
    Call<List<Clase>> getClases(
            @Header("Authorization") String token,
            @Query("location") String location,
            @Query("discipline") String discipline,
            @Query("date") String date
    );
}
