// app/src/main/java/com/example/tp0gym/repository/api/ReservationsApi.java
package com.example.tp0gym.repository.api;

import com.example.tp0gym.repository.dto.CreateReservationBody;
import com.example.tp0gym.repository.dto.ReservationDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReservationsApi {

    // GET /reservations/me?status=CONFIRMED (status opcional)
    @GET("reservations/me")
    Call<List<ReservationDto>> getMyReservations(
            @Header("Authorization") String bearer,
            @Query("status") String status // puede ser null
    );

    // PATCH /reservations/{sessionId}/cancel
    @PATCH("reservations/{sessionId}/cancel")
    Call<ReservationDto> cancelMyReservation(
            @Header("Authorization") String bearer,
            @Path("sessionId") String sessionId
    );

    // POST /reservations  { "sessionId": "..." }
    @POST("reservations")
    Call<ReservationDto> createReservation(
            @Header("Authorization") String bearer,
            @Body CreateReservationBody body
    );
}