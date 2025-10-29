package com.example.carrishop.adaptadores;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.R;
import com.example.carrishop.datos.modelos.ProductoCapturado;

import java.util.ArrayList;
import java.util.Locale;

public class ProductoCapturadoAdaptador extends RecyclerView.Adapter<ProductoCapturadoAdaptador.VH> {

    /** Interfaz para eliminar elementos del carrito */
    public interface OnEliminar {
        void eliminar(int position);
    }

    private final ArrayList<ProductoCapturado> data;
    private final OnEliminar callback;

    public ProductoCapturadoAdaptador(ArrayList<ProductoCapturado> data, OnEliminar cb) {
        this.data = data;
        this.callback = cb;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_voz, parent, false);
        return new VH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductoCapturado item = data.get(position);
        h.txtIndice.setText(String.valueOf(position + 1));

        // 🔹 Mostrar nombre sin "xN"
        h.txtNombre.setText(item.nombre);

        // 🔹 Mostrar cantidad en nueva línea
        h.txtCantidad.setText(String.format(Locale.getDefault(), "Cantidad: %d", item.cantidad));

        // 🔹 Mostrar precio unitario
        h.txtPrecioUnitario.setText(String.format(Locale.getDefault(), "Unitario: S/ %.2f", item.precio));

        // 🔹 Subtotal = precio × cantidad
        double subtotal = item.precio * item.cantidad;
        h.txtSubtotal.setText(String.format(Locale.getDefault(), "Subtotal: S/ %.2f", subtotal));

        // 🔹 Eliminar producto
        h.btnEliminar.setOnClickListener(v -> {
            if (callback != null) callback.eliminar(h.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // =========================
    // ViewHolder interno
    // =========================
    static class VH extends RecyclerView.ViewHolder {
        TextView txtIndice, txtNombre, txtCantidad, txtPrecioUnitario, txtSubtotal;
        ImageButton btnEliminar;

        VH(@NonNull View v) {
            super(v);
            txtIndice = v.findViewById(R.id.txtIndice);
            txtNombre = v.findViewById(R.id.txtNombre);
            txtCantidad = v.findViewById(R.id.txtCantidad);
            txtPrecioUnitario = v.findViewById(R.id.txtPrecioUnitario);
            txtSubtotal = v.findViewById(R.id.txtSubtotal);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}
