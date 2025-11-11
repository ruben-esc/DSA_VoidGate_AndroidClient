package com.example.restclientapp.model;

public class UserLoginRequest {

    private String email;
    private String password;

    public UserLoginRequest(String username, String password) {
        this.email = username;
        this.password = password;

    }

    //GETTERS Y SETTERS
    public String getUsername() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public void setUsername(String username) {
        this.email = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
