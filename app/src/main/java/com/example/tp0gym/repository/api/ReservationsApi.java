package com.example.tp0gym.repository.api;

import com.example.tp0gym.repository.dto.ReservationDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ReservationsApi {

    @GET("reservations/me")
    Call<List<ReservationDto>> getMyReservations(
            @Header("Authorization") String bearer
    );
}