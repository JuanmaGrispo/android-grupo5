package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.User;

import retrofit2.Call;

public class UserRepository {

    private final UserApi api;

    public UserRepository() {
        this.api = RetrofitClient.getClient().create(UserApi.class);
    }

    public Call<User> fetchMe(String token) {
        return api.getMe("Bearer " + token);
    }

    public Call<User> updateMyName(String token, String name) {
        return api.updateName("Bearer " + token, new UserApi.UpdateNameBody(name));
    }
}