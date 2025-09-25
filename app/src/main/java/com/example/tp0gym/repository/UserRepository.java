// repository/UserRepository.java
package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.User;
import com.example.tp0gym.repository.api.UserApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class UserRepository {

    private final UserApi api;

    @Inject
    public UserRepository(UserApi api) {
        this.api = api;
    }

    public Call<User> fetchMe(String token) {
        return api.getMe("Bearer " + token);
    }

    public Call<User> updateMyName(String token, String name) {
        return api.updateName("Bearer " + token, new UserApi.UpdateNameBody(name));
    }
}