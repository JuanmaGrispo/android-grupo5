package com.example.tp0gym.modelo;



public class User {
    private String id;
    private String email;
    private String name;
    private String accessToken;

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
