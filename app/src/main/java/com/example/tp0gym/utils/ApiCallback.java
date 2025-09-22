package com.example.tp0gym.utils;

public interface ApiCallback<T> {
    void onResponse(T response);
    void onError(String errorMessage);
}
