package com.example.tp0gym.repository;

import com.example.tp0gym.modelo.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface UserApi {

    @GET("user/me")
    Call<User> getMe(@Header("Authorization") String bearer);

    class UpdateNameBody {
        public String name;
        public UpdateNameBody(String name) { this.name = name; }
    }

    @PUT("user/me")
    Call<User> updateName(@Header("Authorization") String bearer, @Body UpdateNameBody body);
}
