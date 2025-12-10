package com.example.restclientapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View; // Importante
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
// Asegúrate de importar SharedPreferences si usas MODE_PRIVATE directamente
import android.content.Context;
import android.content.SharedPreferences;

import com.example.restclientapp.api.AuthService;
import com.example.restclientapp.api.RetrofitClient;
import com.example.restclientapp.model.ObjetoCompra;
import com.example.restclientapp.model.Producto;
import com.example.restclientapp.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TiendaActivity extends AppCompatActivity {

    private TextView tvMonedas, btnBack;
    private Button btnTabMercado, btnTabInventario, btnDevAdd;
    private EditText etDevMonedas;
    private RecyclerView recyclerView;
    private ProductosAdapter adapter;

    private String currentUserEmail;
    private User currentUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        SessionManager session = new SessionManager(this);
        currentUserEmail = session.getEmail();

        // Inicializar vistas
        tvMonedas = findViewById(R.id.tvMonedasUsuario);
        btnTabMercado = findViewById(R.id.btnTabMercado);
        btnTabInventario = findViewById(R.id.btnTabInventario);
        btnBack = findViewById(R.id.btnBack);
        recyclerView = findViewById(R.id.recyclerViewProductos);
        etDevMonedas = findViewById(R.id.etDevMonedas);
        btnDevAdd = findViewById(R.id.btnDevAdd);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductosAdapter();
        recyclerView.setAdapter(adapter);

        // --- CORRECCIÓN IMPORTANTE ---
        // Ahora el listener recibe un INT (idProducto), no un String.
        adapter.setListener(idProducto -> realizarCompra(idProducto));

        // Listeners de botones
        btnTabMercado.setOnClickListener(v -> cargarTienda());
        btnTabInventario.setOnClickListener(v -> cargarInventario());
        btnBack.setOnClickListener(v -> finish());
        btnDevAdd.setOnClickListener(v -> inyectarMonedas());

        actualizarDatosUsuario();
        cargarTienda();
    }

    private void actualizarDatosUsuario() {
        AuthService service = RetrofitClient.getApiService();
        Call<User> call = service.getUser(currentUserEmail);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserData = response.body();
                    tvMonedas.setText(currentUserData.getMonedas() + " CR");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) { }
        });
    }

    private void cargarTienda() {
        btnTabMercado.setAlpha(1.0f);
        btnTabInventario.setAlpha(0.5f);

        AuthService service = RetrofitClient.getApiService();
        Call<List<Producto>> call = service.getProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductosAdapter.ItemDisplay> items = new ArrayList<>();
                    for (Producto p : response.body()) {
                        // El constructor ahora extrae p.getId() internamente
                        items.add(new ProductosAdapter.ItemDisplay(p));
                    }
                    adapter.setItems(items);
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarInventario() {
        btnTabMercado.setAlpha(0.5f);
        btnTabInventario.setAlpha(1.0f);

        AuthService service = RetrofitClient.getApiService();
        service.getUser(currentUserEmail).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful() && response.body() != null) {
                    currentUserData = response.body();
                    Map<String, Integer> inventarioMap = currentUserData.getInventario();
                    List<ProductosAdapter.ItemDisplay> items = new ArrayList<>();

                    if (inventarioMap != null) {
                        for (Map.Entry<String, Integer> entry : inventarioMap.entrySet()) {
                            // Constructor de inventario (sin ID)
                            items.add(new ProductosAdapter.ItemDisplay(entry.getKey(), entry.getValue()));
                        }
                    }
                    adapter.setItems(items);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) { }
        });
    }

    // --- LÓGICA DE COMPRA CORREGIDA ---
    private void realizarCompra(int idProducto) {

        // 1. Recuperamos el ID numérico del usuario de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        // IMPORTANTE: Asegúrate de guardar "userId" (int) en tu LoginActivity.
        int idUsuario = prefs.getInt("userId", -1);

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: Sesión inválida. Haz login de nuevo.", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Creamos el objeto con enteros
        ObjetoCompra compra = new ObjetoCompra(idProducto, idUsuario);

        // 3. Llamada a Retrofit
        AuthService service = RetrofitClient.getApiService();
        Call<Void> call = service.comprarProducto(compra);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() || response.code() == 201) {
                    Toast.makeText(TiendaActivity.this, "¡Compra Exitosa!", Toast.LENGTH_SHORT).show();
                    actualizarDatosUsuario(); // Refrescar monedas e inventario
                } else if (response.code() == 402 || response.code() == 409) {
                    // 402 = Payment Required, 409 = Conflict (A veces usado para saldo insuficiente)
                    Toast.makeText(TiendaActivity.this, "Saldo Insuficiente", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TiendaActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inyectarMonedas() {
        String cantidadStr = etDevMonedas.getText().toString();
        if (cantidadStr.isEmpty()) return;

        int cantidad = Integer.parseInt(cantidadStr);
        AuthService service = RetrofitClient.getApiService();
        Call<Void> call = service.updateMonedas(currentUserEmail, cantidad);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TiendaActivity.this, "¡Fondos añadidos!", Toast.LENGTH_SHORT).show();
                    etDevMonedas.setText("");
                    actualizarDatosUsuario();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }
}