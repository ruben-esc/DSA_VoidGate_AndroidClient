package com.example.restclientapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
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
}