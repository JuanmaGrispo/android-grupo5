// di/NetworkModule.java
package com.example.tp0gym.di;

import com.example.tp0gym.repository.AuthApiService;
import com.example.tp0gym.repository.api.ClasesApi;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    private static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";

    @Provides @Singleton
    public HttpLoggingInterceptor provideLogger() {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        return log;
    }

    @Provides @Singleton
    public OkHttpClient provideOkHttp(HttpLoggingInterceptor log) {
        return new OkHttpClient.Builder()
                .addInterceptor(log)
                .build();
    }

    @Provides @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttp) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides @Singleton
    public ClasesApi provideClasesApi(Retrofit retrofit) {
        return retrofit.create(ClasesApi.class);
        // ⬅️ Crea la implementación concreta de tu interfaz de API
    }

    @Provides @Singleton
    public AuthApiService provideAuthApiService(Retrofit retrofit) {
        return retrofit.create(AuthApiService.class); // <-- esto resuelve el MissingBinding
    }
}