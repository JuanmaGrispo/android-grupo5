package com.example.tp0gym.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.tp0gym.modelo.User;
import com.example.tp0gym.repository.AuthRepository;

import javax.inject.Inject;          // ✅ para Hilt
import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Callback;          // ✅ Callback de Retrofit

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }


    public void login(String email, String password, Callback<User> callback) {
        authRepository.login(email, password, callback);
    }
}
