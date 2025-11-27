package com.example.restclientapp.model;

import java.util.Map;
public class User {

    private String id;
    private String nombre; // Devuelto en la respuesta
    private String email;  // Usado en la petición y devuelto en la respuesta
    private String password; // Usado en la petición

    private int monedas;
    private Map<String, Integer> inventario;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {}

    public String getId(){
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public int getMonedas() { return monedas; }

    public Map<String, Integer> getInventario() { return inventario; }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setNombre(String nombre) { this.nombre = nombre; }
}