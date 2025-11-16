package com.example.carrishop.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.R;
import com.example.carrishop.datos.modelos.ListaMercadoItem;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListaMercadoAdapter extends RecyclerView.Adapter<ListaMercadoAdapter.VH> {

    public interface Callbacks {
        void onMarcar(ListaMercadoItem item);
        void onEliminar(ListaMercadoItem item);
    }

    private final List<ListaMercadoItem> datos = new ArrayList<>();
    private final Callbacks callbacks;

    public ListaMercadoAdapter(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void actualizar(List<ListaMercadoItem> nuevos) {
        datos.clear();
        if (nuevos != null) datos.addAll(nuevos);
        notifyDataSetChanged();
    }

    public ListaMercadoItem obtener(int position) {
        return datos.get(position);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_mercado, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ListaMercadoItem item = datos.get(position);
        h.txtNombre.setText(item.nombre);

        if (item.tienePrecio()) {
            String detalle = String.format(Locale.getDefault(),
                    "%d und x S/ %.2f", item.cantidad, item.precio);
            double subtotal = item.precio * item.cantidad;
            h.txtDetalle.setText(detalle + "  •  Subtotal: S/ " + String.format(Locale.getDefault(), "%.2f", subtotal));
        } else {
            h.txtDetalle.setText(R.string.lista_detalle_pendiente);
        }

        h.chkProcesado.setOnCheckedChangeListener(null);
        boolean yaProcesado = item.estado == 1 && item.tienePrecio();
        h.chkProcesado.setChecked(yaProcesado);
        h.chkProcesado.setEnabled(!yaProcesado);
        h.chkProcesado.setText(yaProcesado ? R.string.lista_ya_agregado : R.string.lista_marcar);
        h.chkProcesado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                buttonView.post(() -> buttonView.setChecked(false));
                if (callbacks != null) callbacks.onMarcar(item);
            }
        });

        h.btnEliminar.setOnClickListener(v -> {
            if (callbacks != null) callbacks.onEliminar(item);
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDetalle;
        MaterialCheckBox chkProcesado;
        ImageButton btnEliminar;

        VH(@NonNull View v) {
            super(v);
            txtNombre = v.findViewById(R.id.txtNombreLista);
            txtDetalle = v.findViewById(R.id.txtDetalleLista);
            chkProcesado = v.findViewById(R.id.chkProcesar);
            btnEliminar = v.findViewById(R.id.btnEliminarLista);
        }
    }
}
