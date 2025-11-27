package com.example.restclientapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Añadido
import android.widget.TextView;
import android.widget.Toast;
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

    private TextView tvMonedas;
    private RecyclerView recyclerView;
    private ProductosAdapter adapter;

    // CORRECCIÓN: btnBack ahora es TextView para coincidir con el XML
    private TextView btnBack;
    private Button btnTabMercado, btnTabInventario;

    // Controles de desarrollador
    private EditText etDevMonedas;
    private Button btnDevAdd;

    // Datos en memoria
    private String currentUserEmail;
    private User currentUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        // 1. Obtener email de la sesión
        SessionManager session = new SessionManager(this);
        currentUserEmail = session.getEmail();

        // 2. Inicializar Vistas (IDs deben coincidir con activity_tienda.xml)
        tvMonedas = findViewById(R.id.tvMonedasUsuario);

        btnTabMercado = findViewById(R.id.btnTabMercado);
        btnTabInventario = findViewById(R.id.btnTabInventario);
        btnBack = findViewById(R.id.btnBack);

        recyclerView = findViewById(R.id.recyclerViewProductos);

        // Controles Dev
        etDevMonedas = findViewById(R.id.etDevMonedas);
        btnDevAdd = findViewById(R.id.btnDevAdd);

        // 3. Configurar RecyclerView (Grid de 2 columnas)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductosAdapter();
        recyclerView.setAdapter(adapter);

        // Listener para comprar items (Callback del adaptador)
        adapter.setListener(nombreProducto -> realizarCompra(nombreProducto));

        // 4. Configurar Botones de Pestañas
        btnTabMercado.setOnClickListener(v -> cargarTienda());
        btnTabInventario.setOnClickListener(v -> cargarInventario());

        // Listener botón volver (CORRECCIÓN)
        btnBack.setOnClickListener(v -> finish());

        // Listener Botón Dev (Añadir monedas)
        btnDevAdd.setOnClickListener(v -> inyectarMonedas());

        // 5. Cargar datos iniciales
        actualizarDatosUsuario();
        cargarTienda(); // Empezamos en la tienda
    }

    // --- CARGAR DATOS DEL USUARIO (Monedas e Inventario) ---
    private void actualizarDatosUsuario() {
        AuthService service = RetrofitClient.getApiService();
        Call<User> call = service.getUser(currentUserEmail);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentUserData = response.body();
                    // Actualizar HUD Monedas
                    tvMonedas.setText(currentUserData.getMonedas() + " CR");
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- PESTAÑA: TIENDA ---
    private void cargarTienda() {
        // Visual: Resaltar botón activo (Cambia opacidad)
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
                        items.add(new ProductosAdapter.ItemDisplay(p)); // Convertir a modo Tienda
                    }
                    adapter.setItems(items);
                } else {
                    Toast.makeText(TiendaActivity.this, "La tienda está vacía", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- PESTAÑA: INVENTARIO ---
    private void cargarInventario() {
        // Visual
        btnTabMercado.setAlpha(0.5f);
        btnTabInventario.setAlpha(1.0f);

        // Pedimos datos frescos del usuario para asegurar que el inventario está actualizado
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
                            // Convertir a modo Inventario
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

    // --- ACCIÓN: COMPRAR ---
    private void realizarCompra(String nombreProducto) {
        ObjetoCompra compra = new ObjetoCompra(nombreProducto, currentUserEmail);
        AuthService service = RetrofitClient.getApiService();

        Call<Void> call = service.comprarProducto(compra);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() || response.code() == 201) {
                    Toast.makeText(TiendaActivity.this, "¡Compra Exitosa!", Toast.LENGTH_SHORT).show();
                    actualizarDatosUsuario(); // Refrescar monedas
                } else if (response.code() == 402) {
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

    // --- ACCIÓN: INYECTAR MONEDAS (DEV) ---
    private void inyectarMonedas() {
        String cantidadStr = etDevMonedas.getText().toString();

        // Validación básica
        if (cantidadStr.isEmpty()) {
            Toast.makeText(this, "Escribe una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);

        // Llamada al Backend
        AuthService service = RetrofitClient.getApiService();
        Call<Void> call = service.updateMonedas(currentUserEmail, cantidad);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TiendaActivity.this, "¡Fondos inyectados! 💰", Toast.LENGTH_SHORT).show();
                    etDevMonedas.setText(""); // Limpiar caja

                    // IMPORTANTE: Refrescar la pantalla para ver el dinero nuevo
                    actualizarDatosUsuario();
                } else {
                    Toast.makeText(TiendaActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TiendaActivity.this, "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}