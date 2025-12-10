package com.example.restclientapp;

import android.content.Context;
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

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    public static class ItemDisplay {
        int id; // <--- NUEVO: ID NUMÉRICO
        String nombre;
        int precio;
        int cantidad;
        boolean esTienda;

        // Constructor TIENDA (Extraemos el ID del producto)
        public ItemDisplay(Producto p) {
            this.id = p.getId(); // Asegúrate de tener getId() en tu clase Producto
            this.nombre = p.getNombreproducto();
            this.precio = p.getPrecio();
            this.esTienda = true;
        }

        // Constructor INVENTARIO (No hay ID de compra, ponemos -1)
        public ItemDisplay(String nombre, int cantidad) {
            this.id = -1;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.esTienda = false;
        }
    }

    private List<ItemDisplay> listaItems = new ArrayList<>();
    private OnItemClickListener listener;

    // INTERFAZ ACTUALIZADA: Recibe int en vez de String
    public interface OnItemClickListener {
        void onComprarClick(int idProducto);
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

        // Cargar imagen (Tu lógica de imágenes se mantiene igual)
        int resId = obtenerIdImagen(holder.itemView.getContext(), item.nombre);
        holder.imgProducto.setImageResource(resId);

        if (item.esTienda) {
            holder.tvPrecio.setText(item.precio + " CR");
            holder.tvPrecio.setVisibility(View.VISIBLE);
            holder.btnComprar.setVisibility(View.VISIBLE);
            holder.tvCantidad.setVisibility(View.GONE);

            // CLICK: Pasamos el ID (int)
            holder.btnComprar.setOnClickListener(v -> {
                if (listener != null) listener.onComprarClick(item.id);
            });

        } else {
            holder.tvPrecio.setVisibility(View.GONE);
            holder.btnComprar.setVisibility(View.GONE);
            holder.tvCantidad.setText("x" + item.cantidad);
            holder.tvCantidad.setVisibility(View.VISIBLE);
        }
    }

    // ... (Mantén tu método obtenerIdImagen tal cual lo tenías) ...
    private int obtenerIdImagen(Context context, String nombreItem) {
        String nombreNormalizado = (nombreItem != null) ? nombreItem.toLowerCase() : "default";
        String nombreDrawable;

        if (nombreNormalizado.contains("katana")) nombreDrawable = "katana";
        else if (nombreNormalizado.contains("jeringuilla")) nombreDrawable = "jeringuilla";
        else if (nombreNormalizado.contains("chaleco")) nombreDrawable = "chaleco";
        else if (nombreNormalizado.contains("cubo")) nombreDrawable = "energia";
        else nombreDrawable = "ic_launcher_foreground";

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
            btnComprar = itemView.findViewById(R.id.btnAccion);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}