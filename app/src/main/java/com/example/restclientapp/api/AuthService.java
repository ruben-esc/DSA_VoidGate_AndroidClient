package com.example.restclientapp.api;

import com.example.restclientapp.model.UserLoginRequest;
import com.example.restclientapp.model.LoginResponse;
import com.example.restclientapp.model.User;
import com.example.restclientapp.model.Verificacion;
import com.example.restclientapp.model.ObjetoCompra;
import com.example.restclientapp.model.Producto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;
import retrofit2.http.Path;

public interface AuthService {

    @POST("usuarios/login")
    Call<User> login(@Body User request);

    @POST("usuarios/register")
    Call<Void> register(@Body User request);
    @POST("usuarios/verificar-codigo")
    Call<Void> verifyAccount(@Body Verificacion verificacion);

    @GET("usuarios/{email}")
    Call<User> getUser(@Path("email") String email);

    @GET("productos/")
    Call<List<Producto>> getProductos();

    @POST("productos/comprar")
    Call<Void> comprarProducto(@Body ObjetoCompra compra);

    @retrofit2.http.PUT("usuarios/monedas/{email}/{cantidad}")
    Call<Void> updateMonedas(@retrofit2.http.Path("email") String email, @retrofit2.http.Path("cantidad") int cantidad);
}
