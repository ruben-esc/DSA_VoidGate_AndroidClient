package com.example.restclientapp.model;

public class LoginResponse {
    // LOS NOMBRES DE CAMPO DEBEN COINCIDIR CON
    // LO QUE DEVUELVE EL BACKEND
    private String token;
    private String message;

    public String getToken() {
        return token;
    }

    //Por si decidimos mostrar mensaje al usuario
    public String getMessage(){
        return message;

    }
}
