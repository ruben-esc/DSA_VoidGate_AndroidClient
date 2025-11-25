package com.example.restclientapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    // Nombre del archivo de preferencias
    private static final String PREF_NAME = "JuegoSession";

    // Claves para los datos que queremos guardar
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    public SessionManager(Context context) {
        this.context = context;
        // MODE_PRIVATE: Solo esta app puede leer estos datos
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Método para guardar la sesión cuando el login es correcto
    public void guardarSesion(String email, String password) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply(); // Guardamos los cambios asíncronamente
    }

    // Método para comprobar si está logueado
    public boolean estalogueado() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Obtener el email del usuario actual (útil para hacer compras)
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    // Obtener password (si la necesitas para validar compras en el backend)
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    // Cerrar sesión (Borrar datos)
    public void cerrarSesion() {
        editor.clear();
        editor.commit();
    }
}