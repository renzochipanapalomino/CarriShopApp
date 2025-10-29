package com.example.carrishop.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.ActividadDetalleProducto;
import com.example.carrishop.R;
import com.example.carrishop.datos.modelos.Producto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Adaptador para mostrar productos en las sugerencias (ofertas o todos).
 * Compatible con el nuevo diseño de tarjetas verticales con botón flotante.
 */
public class ProductoAdaptador extends RecyclerView.Adapter<ProductoAdaptador.VH> {

    private final List<Producto> data = new ArrayList<>();

    public void submit(List<Producto> nuevos) {
        data.clear();
        if (nuevos != null) data.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sugerencia, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int i) {
        Producto p = data.get(i);
        Context c = h.itemView.getContext();

        // Imagen del producto
        Drawable d;
        try {
            d = ContextCompat.getDrawable(c, p.imagenRes);
        } catch (Exception e) {
            d = ContextCompat.getDrawable(c, R.drawable.ic_image_placeholder);
        }
        h.img.setImageDrawable(d);

        // Nombre y precio
        h.nombre.setText(p.nombre);
        h.precio.setText(String.format("S/ %.2f", p.precio));

        // Click en la tarjeta → abre detalle
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(c, ActividadDetalleProducto.class);
            intent.putExtra("id", p.id);
            intent.putExtra("nombre", p.nombre);
            intent.putExtra("precio", p.precio);
            intent.putExtra("imagen", p.imagenRes);
            intent.putExtra("descripcion", generarDescripcion(p.nombre, p.oferta));
            c.startActivity(intent);
        });

        // Botón flotante → agregar directo al carrito
        h.btnAgregar.setOnClickListener(v -> {
            var sp = c.getSharedPreferences("carrito", Context.MODE_PRIVATE);
            var set = new HashSet<>(sp.getStringSet("items", new HashSet<>()));
            set.add(p.nombre + "|" + p.precio + "|" + p.imagenRes);
            sp.edit().putStringSet("items", set).apply();

            Toast.makeText(c, "Añadido al carrito", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // =======================
    // ViewHolder interno
    // =======================
    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView nombre, precio;
        FloatingActionButton btnAgregar;

        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgProd);
            nombre = v.findViewById(R.id.txtNombreProd);
            precio = v.findViewById(R.id.txtPrecioProd);
            btnAgregar = v.findViewById(R.id.btnAdquirir);
        }
    }

    // =======================
    // Descripción simulada
    // =======================
    private String generarDescripcion(String nombre, boolean oferta) {
        if (oferta) {
            return "¡Oferta especial! El producto " + nombre +
                    " cuenta con un descuento disponible por tiempo limitado.";
        } else {
            return "El producto " + nombre +
                    " está disponible en tu supermercado habitual. Excelente calidad garantizada.";
        }
    }
}
