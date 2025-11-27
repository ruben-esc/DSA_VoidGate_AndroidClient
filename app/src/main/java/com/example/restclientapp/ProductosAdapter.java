package com.example.restclientapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.restclientapp.model.Producto;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    // Modelo auxiliar para manejar tanto productos de tienda como items de inventario
    public static class ItemDisplay {
        String nombre;
        int precio;     // Solo para tienda
        int cantidad;   // Solo para inventario
        boolean esTienda; // true = modo tienda, false = modo inventario

        // Constructor para TIENDA
        public ItemDisplay(Producto p) {
            this.nombre = p.getNombreproducto();
            this.precio = p.getPrecio();
            this.esTienda = true;
        }
        // Constructor para INVENTARIO
        public ItemDisplay(String nombre, int cantidad) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.esTienda = false;
        }
    }

    private List<ItemDisplay> listaItems = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onComprarClick(String nombreProducto);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ItemDisplay> nuevosItems) {
        this.listaItems = nuevosItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemDisplay item = listaItems.get(position);

        holder.tvNombre.setText(item.nombre);

        // LÓGICA VISUAL SEGÚN SI ES TIENDA O INVENTARIO (Esto lo dejamos igual, está perfecto)
        if (item.esTienda) {
            holder.tvPrecio.setText(item.precio + " CR");
            holder.tvPrecio.setVisibility(View.VISIBLE);
            holder.btnComprar.setVisibility(View.VISIBLE);
            holder.tvCantidad.setVisibility(View.GONE);
        } else {
            holder.tvPrecio.setVisibility(View.GONE);
            holder.btnComprar.setVisibility(View.GONE);
            holder.tvCantidad.setText("x" + item.cantidad);
            holder.tvCantidad.setVisibility(View.VISIBLE);
        }

        // --- AQUÍ ESTÁ EL CAMBIO IMPORTANTE ---
        // Usamos la función auxiliar para cargar tus imágenes VOID-GATE
        int resId = obtenerIdImagen(holder.itemView.getContext(), item.nombre);
        holder.imgProducto.setImageResource(resId);
        // --------------------------------------

        // Click en comprar
        holder.btnComprar.setOnClickListener(v -> {
            if (listener != null) listener.onComprarClick(item.nombre);
        });
    }

    /**
     * Método auxiliar para mapear el nombre del producto (String)
     * al recurso gráfico en res/drawable (int)
     */
    private int obtenerIdImagen(Context context, String nombreItem) {
        // Nombre del archivo PNG (sin extensión) que vamos a buscar
        String nombreDrawable;

        // Convertimos a minúsculas para evitar problemas (Machete vs machete)
        String nombreNormalizado = nombreItem.toLowerCase();

        // LÓGICA DE MAPEO (Fusionando sus nombres con tus imágenes)
        if (nombreNormalizado.contains("katana")) {
            nombreDrawable = "katana";
        }
        else if (nombreNormalizado.contains("jeringuilla")) {
            nombreDrawable = "jeringuilla";
        }
        else if (nombreNormalizado.contains("chaleco")) {
            nombreDrawable = "chaleco";
        }
        else if (nombreNormalizado.contains("cubo de energia")) {
            nombreDrawable = "energia";
        }
        else {
            // Si no coincide con nada, ponemos el icono de la app por defecto
            nombreDrawable = "ic_launcher_foreground";
        }

        // Devuelve el ID numérico de la imagen para que Android la pinte
        return context.getResources().getIdentifier(nombreDrawable, "drawable", context.getPackageName());
    }

    @Override
    public int getItemCount() { return listaItems.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvCantidad;
        Button btnComprar;
        ImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnComprar = itemView.findViewById(R.id.btnAccion); // Se llama btnAccion en tu XML anterior
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}