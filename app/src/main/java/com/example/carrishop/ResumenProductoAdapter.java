package com.example.carrishop;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.datos.modelos.ProductoCapturado;

import java.util.List;
import java.util.Locale;

public class ResumenProductoAdapter extends RecyclerView.Adapter<ResumenProductoAdapter.VH> {

    private final List<ProductoCapturado> data;

    public ResumenProductoAdapter(List<ProductoCapturado> d) {
        this.data = d;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resumen_producto, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ProductoCapturado p = data.get(position);
        holder.indice.setText(String.valueOf(position + 1));

        // 🔹 Nombre del producto
        String nombreProducto = (p.nombre != null && !p.nombre.isEmpty()) ? p.nombre : "Producto sin nombre";
        holder.nombre.setText(nombreProducto);

        // 🔹 Cantidad y subtotales
        int cantidad = (p.cantidad > 0) ? p.cantidad : 1;
        double subtotal = cantidad * p.precio;

        // 🔹 Detalle multilínea
        String detalle = String.format(Locale.US,
                "Cantidad: %d\nUnitario: S/ %.2f\nSubtotal: S/ %.2f",
                cantidad, p.precio, subtotal);

        // 🔹 Subrayar y colorear solo la línea de “Subtotal”
        SpannableString spannable = new SpannableString(detalle);
        int start = detalle.indexOf("Subtotal:");
        if (start >= 0) {
            int end = detalle.length();
            spannable.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.primario);
            spannable.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.detalle.setText(spannable);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView indice, nombre, detalle;

        VH(@NonNull View v) {
            super(v);
            indice = v.findViewById(R.id.txtIndiceR);
            nombre = v.findViewById(R.id.txtNombreR);
            detalle = v.findViewById(R.id.txtPrecioR);
        }
    }
}
