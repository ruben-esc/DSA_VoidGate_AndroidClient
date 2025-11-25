package com.example.restclientapp.model;

public class Verificacion {
    private String email;
    private String codigo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Verificacion(String email, String codigo) {
        this.email = email;
        this.codigo = codigo;
    }
}
