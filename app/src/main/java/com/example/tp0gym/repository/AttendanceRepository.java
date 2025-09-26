package com.example.tp0gym.repository;

import com.example.tp0gym.repository.AuthApiService;
import com.example.tp0gym.modelo.AttendanceDto;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit2.Call;

@Singleton
public class AttendanceRepository {

    private final AuthApiService api;

    @Inject
    public AttendanceRepository(AuthApiService api) {
        this.api = api;
    }

    public Call<List<AttendanceDto>> getMyAttendance(String token) {
        return api.getMyAttendance("Bearer " + token);
    }
}
