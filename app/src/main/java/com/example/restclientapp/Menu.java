package com.example.restclientapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            Button btnLogout = findViewById(R.id.btnLogout);

            btnLogout.setOnClickListener(v -> {
                // A. Borramos los datos de la sesión
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.cerrarSesion();

                // B. Volvemos a la pantalla de Login
                // (Asegúrate de que tu login se llama LoginActivity.class)
                Intent intent = new Intent(Menu.this, MainActivity.class);
                startActivity(intent);

                // C. Cerramos el Menú para que no pueda volver atrás
                finish();
            });

            Button btnTienda = findViewById(R.id.btnTienda);

            btnTienda.setOnClickListener(v -> {
                // Creamos el intento para ir a la actividad Tienda
                Intent intent = new Intent(Menu.this, TiendaActivity.class);
                startActivity(intent);

                // NOTA: Aquí NO ponemos finish().
                // Queremos que si el usuario da al botón "Atrás" en la tienda,
                // vuelva a este Menú, no se salga de la app.
            });
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Aquí llamamos al diálogo para preguntar si quiere salir
                mostrarDialogoSalida();
            }
        };
        // Registramos el callback
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private void mostrarDialogoSalida() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Estás seguro de que quieres salir de la aplicación?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cierra toda la aplicación
                        finishAffinity();
                    }
                })
                .setNegativeButton("No", null) // No hace nada, solo cierra el diálogo
                .show();
    }
}