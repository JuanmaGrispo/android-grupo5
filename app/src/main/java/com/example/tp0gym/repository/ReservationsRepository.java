// app/src/main/java/com/example/tp0gym/repository/ReservationsRepository.java
package com.example.tp0gym.repository;

import com.example.tp0gym.repository.api.ReservationsApi;
import com.example.tp0gym.repository.dto.ReservationDto;
import com.example.tp0gym.utils.AppPreferences;

import java.util.List;

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

    private String bearer() {
        String t = prefs.getToken();
        return (t == null || t.isEmpty()) ? "" : "Bearer " + t;
    }

    /** Trae las reservas del usuario autenticado. */
    public Call<List<ReservationDto>> fetchMyReservations(String status) {
        return api.getMyReservations(bearer());
    }

    // Si luego tu backend agrega filtros por estado, podrías ampliar el API y el repo así:
    // public Call<List<ReservationDto>> fetchMyReservations(@Nullable String status) {
    //     return api.getMyReservations(bearer(), status);
    // }
}