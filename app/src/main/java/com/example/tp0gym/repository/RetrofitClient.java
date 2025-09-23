package com.example.tp0gym.repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // para emulador uso esta
    private static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";

    //para celular uso esta

    //private static final String BASE_URL = "http://192.168.1.13:3000/api/v1/";


    private static Retrofit retrofit;

    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static AuthApiService getAuthApiService() {
        return getRetrofit().create(AuthApiService.class);
    }
}
