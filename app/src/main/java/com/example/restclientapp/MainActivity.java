package com.example.restclientapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.core.splashscreen.SplashScreen;

import com.example.restclientapp.api.AuthService;
import com.example.restclientapp.api.RetrofitClient;
import com.example.restclientapp.model.User;
import com.example.restclientapp.model.Verificacion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private EditText codigoEditText;
    private Button verificarButton;
    private String ultimoemailregistrado = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        registerButton = findViewById(R.id.button_register);
        codigoEditText = findViewById(R.id.editTextNumber);
        verificarButton = findViewById(R.id.button);


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
        verificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performVerify();
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
    private void performRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce el email y la contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }
        User registerRequest = new User(email, password);
        AuthService service = RetrofitClient.getApiService();
        Call<Void> call = service.register(registerRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 201 (Registro exitoso)
                    Toast.makeText(MainActivity.this,
                            "Registro exitoso. Revisa tu email para verificar tu cuenta.",
                            Toast.LENGTH_LONG).show();


                    ultimoemailregistrado = email;
                    return;
                }

                // 409 → email ya registrado
                if (response.code() == 409) {
                    Toast.makeText(MainActivity.this,
                            "Ese email ya está registrado.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // 400 → campos inválidos o email mal formateado
                if (response.code() == 400) {
                    Toast.makeText(MainActivity.this,
                            "Datos inválidos o incompletos.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Otros errores
                Log.e("REGISTER", "Error HTTP: " + response.code());
                Toast.makeText(MainActivity.this,
                        "Error en la petición: " + response.code(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("REGISTER", "Fallo de conexión: " + t.getMessage());
                Toast.makeText(MainActivity.this,
                        "Error de red: ¿El servidor está corriendo?",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
        private void performVerify() {

            // Si no hay email registrado aún
            if (ultimoemailregistrado == null) {
                Toast.makeText(this, "Primero registra un usuario.", Toast.LENGTH_SHORT).show();
                return;
            }

            String codigo = codigoEditText.getText().toString().trim();
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Introduce el código de verificación.", Toast.LENGTH_SHORT).show();
                return;
            }
            Verificacion verificacion = new Verificacion(ultimoemailregistrado, codigo);
            AuthService service = RetrofitClient.getApiService();
            Call<Void> call = service.verifyAccount(verificacion);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this,
                                "Cuenta verificada correctamente. Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG).show();

                        // Ocultar campos de verificación tras éxito
                        codigoEditText.setVisibility(View.GONE);
                        verificarButton.setVisibility(View.GONE);

                        return;
                    }

                    // Código incorrecto
                    if (response.code() == 401) {
                        Toast.makeText(MainActivity.this,
                                "Código incorrecto.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Otros errores
                    Toast.makeText(MainActivity.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MainActivity.this,
                            "Error de conexión: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }

}