package com.example.restclientapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView; // Importante para los botones de texto
import android.widget.Toast;
import androidx.core.splashscreen.SplashScreen;
import android.content.Intent;

import com.example.restclientapp.api.AuthService;
import com.example.restclientapp.api.RetrofitClient;
import com.example.restclientapp.model.User;
import com.example.restclientapp.model.Verificacion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // --- VARIABLES DE LAYOUT (PANTALLAS) ---
    private View layoutLogin, layoutRegister, layoutVerificacion;

    // --- VARIABLES DE LOGIN ---
    private EditText etEmailLogin, etPasswordLogin;
    private Button btnLogin;

    // --- VARIABLES DE REGISTRO ---
    private EditText etNameRegister, etEmailRegister, etPasswordRegister;
    private Button btnRegister;

    // --- VARIABLES DE VERIFICACIÓN ---
    private EditText etEmailVerify, etCodeVerify;
    private Button btnVerify;

    private ProgressBar progressBar;

    // Variable auxiliar para guardar el email tras registro
    private String ultimoEmailRegistrado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // 1. Verificar Sesión
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.estalogueado()) {
            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish();
            return;
        }

        // 2. Cargar el XML nuevo
        setContentView(R.layout.activity_main);

        // 3. INICIALIZAR TODAS LAS VISTAS (Vinculamos con el XML nuevo)
        initViews();

        // 4. CONFIGURAR LA NAVEGACIÓN ENTRE PANTALLAS (LO QUE PEDISTE)
        setupNavigation();

        // 5. LISTENERS DE LOS BOTONES DE ACCIÓN
        btnLogin.setOnClickListener(v -> performLogin());
        btnRegister.setOnClickListener(v -> performRegister());
        btnVerify.setOnClickListener(v -> performVerify());
    }

    // --- MÉTODO AUXILIAR PARA VINCULAR VISTAS ---
    private void initViews() {
        // Layouts contenedores
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);
        layoutVerificacion = findViewById(R.id.layoutVerificacion);

        // Login Inputs
        etEmailLogin = findViewById(R.id.edit_text_email_login);
        etPasswordLogin = findViewById(R.id.edit_text_password_login);
        btnLogin = findViewById(R.id.button_login);

        // Register Inputs
        etNameRegister = findViewById(R.id.edit_text_name_register);
        etEmailRegister = findViewById(R.id.edit_text_email_register);
        etPasswordRegister = findViewById(R.id.edit_text_password_register);
        btnRegister = findViewById(R.id.button_register);

        // Verify Inputs
        etEmailVerify = findViewById(R.id.edit_text_email_verify);
        etCodeVerify = findViewById(R.id.edit_text_code);
        btnVerify = findViewById(R.id.button_verify); // Asegúrate que en el XML el ID es button_verify (o button)

        // Progress Bar
        progressBar = findViewById(R.id.progressBar);
    }

    // --- MÉTODO PARA LA NAVEGACIÓN VISUAL ---
    private void setupNavigation() {
        // Botones de texto para cambiar de pantalla
        TextView btnGoToRegister = findViewById(R.id.btnGoToRegister);
        TextView btnGoToVerify = findViewById(R.id.btnGoToVerify);
        TextView btnBackToLogin1 = findViewById(R.id.btnBackToLoginFromRegister);
        TextView btnBackToLogin2 = findViewById(R.id.btnBackToLoginFromVerify);

        // Ir a Registro
        btnGoToRegister.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);
            layoutVerificacion.setVisibility(View.GONE);
        });

        // Ir a Verificación Manual
        btnGoToVerify.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.GONE);
            layoutVerificacion.setVisibility(View.VISIBLE);

            // Si ya escribieron el email en el login, lo copiamos aquí por comodidad
            if(etEmailLogin.getText().length() > 0) {
                etEmailVerify.setText(etEmailLogin.getText().toString());
            }
        });

        // Volver al Login (Lógica común)
        View.OnClickListener volverLogin = v -> {
            layoutLogin.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.GONE);
            layoutVerificacion.setVisibility(View.GONE);
        };

        btnBackToLogin1.setOnClickListener(volverLogin);
        btnBackToLogin2.setOnClickListener(volverLogin);
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
            btnVerify.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
            btnVerify.setEnabled(true);
        }
    }

    // --- LÓGICA DE LOGIN ---
    private void performLogin() {
        // OJO: Cogemos los datos del layout de LOGIN
        String email = etEmailLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce el email y la contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        User loginRequest = new User(email, password);
        showLoading(true);

        AuthService service = RetrofitClient.getApiService();
        Call<User> call = service.login(loginRequest);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(MainActivity.this, "Login exitoso.", Toast.LENGTH_SHORT).show();

                    // Guardar sesión
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    sessionManager.guardarSesion(email, password);

                    // Ir al Menu
                    Intent intent = new Intent(MainActivity.this, Menu.class);
                    startActivity(intent);
                    finish();

                } else if (response.code() == 401) {
                    Toast.makeText(MainActivity.this, "Credenciales inválidas.", Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    // Si el usuario no está verificado, le mandamos a la pantalla de verificar
                    Toast.makeText(MainActivity.this, "Email no verificado. Introduce el código.", Toast.LENGTH_LONG).show();

                    layoutLogin.setVisibility(View.GONE);
                    layoutVerificacion.setVisibility(View.VISIBLE);
                    etEmailVerify.setText(email); // Rellenamos el email automáticamente
                    ultimoEmailRegistrado = email;

                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
                Log.e("LOGIN", t.getMessage());
            }
        });
    }

    // --- LÓGICA DE REGISTRO ---
    private void performRegister() {
        // OJO: Cogemos los datos del layout de REGISTRO
        String nombre = etNameRegister.getText().toString().trim();
        String email = etEmailRegister.getText().toString().trim();
        String password = etPasswordRegister.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        User registerRequest = new User();
        registerRequest.setNombre(nombre);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);

        AuthService service = RetrofitClient.getApiService();
        Call<Void> call = service.register(registerRequest);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registro exitoso. Verifica tu email.", Toast.LENGTH_LONG).show();

                    ultimoEmailRegistrado = email;

                    // CAMBIO AUTOMÁTICO DE PANTALLA
                    layoutRegister.setVisibility(View.GONE);
                    layoutVerificacion.setVisibility(View.VISIBLE);

                    // Pre-rellenamos el email en la verificación
                    etEmailVerify.setText(email);

                    return;
                }

                if (response.code() == 409) {
                    Toast.makeText(MainActivity.this, "Ese email ya está registrado.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Fallo de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LÓGICA DE VERIFICACIÓN ---
    private void performVerify() {
        // OJO: Cogemos los datos del layout de VERIFICACIÓN
        String email = etEmailVerify.getText().toString().trim();
        String codigo = etCodeVerify.getText().toString().trim();

        if (email.isEmpty() || codigo.isEmpty()) {
            Toast.makeText(this, "Faltan datos.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        Verificacion verificacion = new Verificacion(email, codigo);
        AuthService service = RetrofitClient.getApiService();
        Call<Void> call = service.verifyAccount(verificacion);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Cuenta verificada. Inicia sesión.", Toast.LENGTH_LONG).show();

                    // Volver al Login automáticamente
                    layoutVerificacion.setVisibility(View.GONE);
                    layoutLogin.setVisibility(View.VISIBLE);

                    // Rellenar el login para facilitar
                    etEmailLogin.setText(email);
                    etPasswordLogin.setText(""); // Por seguridad limpiar pass
                    return;
                }

                if (response.code() == 401 || response.code() == 400) {
                    Toast.makeText(MainActivity.this, "Código incorrecto.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}