package com.example.restclientapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.splashscreen.SplashScreen;
import android.content.Intent;

import com.example.restclientapp.api.AuthService;
import com.example.restclientapp.api.RetrofitClient;
import com.example.restclientapp.model.User;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // Verificar si ya hay sesión iniciada
        SessionManager sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.estalogueado()) {
            // ¡Ya está logueado! Vamos directo a la tienda/juego
            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish(); // Cerramos esta actividad para que no vuelva aquí
            return; // Importante para que no cargue el layout del login
        }

        // Si no está logueado, mostramos la pantalla de Login normal
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegister();
            }
        });
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce el email y la contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }
        User loginRequest = new User(email, password);

        AuthService service = RetrofitClient.getApiService();
        Call<User> call = service.login(loginRequest);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 200 (Login exitoso).
                    User loggedInUser = response.body();

                    Toast.makeText(MainActivity.this,
                            "Login exitoso. Bienvenid@ de nuevo",
                            Toast.LENGTH_LONG).show();

                    // Guardar el token en SharedPreferences
                    if (response.isSuccessful()) {
                        // 1. Instanciar el SessionManager
                        SessionManager sessionManager = new SessionManager(getApplicationContext());

                        // 2. Guardar los datos del usuario que acabas de loguear
                        // Supongamos que usaste variables 'emailInput' y 'passwordInput'
                        sessionManager.guardarSesion(email, password);

                        // 3. Ir a la pantalla principal del juego/tienda
                        Intent intent = new Intent(MainActivity.this, Menu.class);
                        startActivity(intent);
                        // 4. IMPORTANTE: Cerrar el Login para que no se pueda volver atrás
                        finish();
                    }

                } else if (response.code() == 401) {
                    // ERROR 401: Credenciales inválidas (email o contraseña incorrectos)
                    Toast.makeText(MainActivity.this, "Credenciales inválidas. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                } else {
                    // ERROR HTTP (400, 404, 500)
                    Log.e("LOGIN", "Error HTTP: " + response.code());
                    Toast.makeText(MainActivity.this, "Error de servidor o petición: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //FALLO DE CONEXIÓN: Error de red o servidor no disponible.
                Log.e("LOGIN", "Fallo de conexión: " + t.getMessage());
                Toast.makeText(MainActivity.this,
                        "Error de red: ¿Está el servidor corriendo en 10.0.2.2:8080/dsaApp/ ?",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
    private void performRegister(){
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce el email y la contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }
        User registerRequest = new User(email, password);
        AuthService service = RetrofitClient.getApiService();
        Call<User> call = service.register(registerRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 200 (Registro exitoso)
                    Toast.makeText(MainActivity.this, "Registro exitoso. Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show();

                }
                else if (response.code() == 401) {
                    // ERROR 401: Credenciales inválidas (email o contraseña incorrectos)
                    Toast.makeText(MainActivity.this, "Credenciales inválidas. Inténtalo de nuevo.", Toast.LENGTH_LONG).show();
                } else {
                    // ERROR HTTP (400, 404, 500)
                    Log.e("LOGIN", "Error HTTP: " + response.code());
                    Toast.makeText(MainActivity.this, "Error de servidor o petición: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //FALLO DE CONEXIÓN: Error de red o servidor no disponible.
                Log.e("LOGIN", "Fallo de conexión: " + t.getMessage());
                Toast.makeText(MainActivity.this,
                        "Error de red: ¿Está el servidor corriendo en 10.0.2.2:8080/dsaApp/ ?",
                        Toast.LENGTH_LONG).show();
            }
        });



    }
}