package com.example.carrishop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.datos.entidades.CompraEntidad;
import com.example.carrishop.R;

import java.util.List;
import java.util.Locale;

public class HistorialCompraAdapter extends RecyclerView.Adapter<HistorialCompraAdapter.ViewHolder> {

    private final List<CompraEntidad> lista;

    public HistorialCompraAdapter(List<CompraEntidad> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_compra, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        CompraEntidad c = lista.get(pos);
        h.txtFecha.setText(c.fechaHora);
        h.txtProductos.setText("Productos: " + c.cantidadProductos);
        h.txtTotal.setText(String.format(Locale.US, "S/ %.2f", c.total));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtProductos, txtTotal;
        ViewHolder(View v) {
            super(v);
            txtFecha = v.findViewById(R.id.txtFecha);
            txtProductos = v.findViewById(R.id.txtProductos);
            txtTotal = v.findViewById(R.id.txtTotal);
        }
    }
}
