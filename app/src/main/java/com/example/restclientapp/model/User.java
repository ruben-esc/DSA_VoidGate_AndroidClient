package com.example.restclientapp.model;
public class User {

    private String id;
    private String nombre; // Devuelto en la respuesta
    private String email;  // Usado en la petición y devuelto en la respuesta
    private String password; // Usado en la petición

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getId(){
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

}