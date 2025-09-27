// app/src/main/java/com/example/tp0gym/repository/ReservationsRepository.java
package com.example.tp0gym.repository;

import com.example.tp0gym.repository.api.ReservationsApi;
import com.example.tp0gym.repository.dto.ReservationDto;
import com.example.tp0gym.utils.AppPreferences;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class ReservationsRepository {

    private final ReservationsApi api;
    private final AppPreferences prefs;

    @Inject
    public ReservationsRepository(ReservationsApi api, AppPreferences prefs) {
        this.api = api;
        this.prefs = prefs;
    }

    public Call<List<ReservationDto>> fetchMyReservations(@Nullable String status) {
        String bearer = "Bearer " + prefs.getToken();
        return api.getMyReservations(bearer, status);
    }

    public Call<ReservationDto> cancelMyReservation(String sessionId) {
        String bearer = "Bearer " + prefs.getToken();
        return api.cancelMyReservation(bearer, sessionId);
    }
}