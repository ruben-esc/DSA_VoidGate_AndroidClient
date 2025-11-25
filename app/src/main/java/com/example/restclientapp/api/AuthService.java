package com.example.restclientapp.api;

import com.example.restclientapp.model.UserLoginRequest;
import com.example.restclientapp.model.LoginResponse;
import com.example.restclientapp.model.User;
import com.example.restclientapp.model.Verificacion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    @POST("usuarios/login")
    Call<User> login(@Body User request);

    @POST("usuarios/register")
    Call<Void> register(@Body User request);
    @POST("usuarios/verificar-codigo")
    Call<Void> verifyAccount(@Body Verificacion verificacion);
}
