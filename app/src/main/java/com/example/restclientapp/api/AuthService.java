package com.example.restclientapp.api;

import com.example.restclientapp.model.UserLoginRequest;
import com.example.restclientapp.model.LoginResponse;
import com.example.restclientapp.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface AuthService {

    @POST("usuarios/login")
    Call<User> login(@Body User request);
}
