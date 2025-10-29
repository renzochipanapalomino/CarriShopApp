package com.example.carrishop.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.R;
import com.example.carrishop.datos.modelos.SupermercadoModelo;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SupermercadoAdaptador extends RecyclerView.Adapter<SupermercadoAdaptador.VH> {

    /** Interfaz para capturar los clics en los items */
    public interface OnItemClick {
        void onClick(SupermercadoModelo item);
    }

    private final List<SupermercadoModelo> data;
    private final OnItemClick onItemClick;

    public SupermercadoAdaptador(List<SupermercadoModelo> data, OnItemClick onItemClick) {
        this.data = data;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermercado, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        SupermercadoModelo m = data.get(position);

        // Imagen principal
        h.imgFondo.setImageResource(m.logoRes);

        // Nombre y descripción
        h.nombre.setText(m.nombre);
        h.descripcion.setText(m.descripcion);

        // Cambiar color de fondo según supermercado
        int colorFondo;
        switch (m.nombre) {
            case "Tottus":
                colorFondo = 0xFFE6F4EA; // verde suave
                break;
            case "Plaza Vea":
                colorFondo = 0xFFFFE6E6; // rojo pastel
                break;
            case "Saga Falabella":
                colorFondo = 0xFFE9F7EF; // verde lima claro
                break;
            case "Ripley":
                colorFondo = 0xFFEDE7F6; // lila suave
                break;
            case "Mass":
                colorFondo = 0xFFFFF9E6; // amarillo claro
                break;
            default:
                colorFondo = 0xFFFFFFFF;
        }
        h.itemView.setBackgroundColor(colorFondo);

        // Listener para clics
        View.OnClickListener click = v -> {
            if (onItemClick != null) onItemClick.onClick(m);
        };
        h.itemView.setOnClickListener(click);
        h.btnIngresar.setOnClickListener(click);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /** Clase interna ViewHolder */
    static class VH extends RecyclerView.ViewHolder {
        ImageView imgFondo;
        TextView nombre, descripcion;
        MaterialButton btnIngresar;

        VH(@NonNull View itemView) {
            super(itemView);
            imgFondo = itemView.findViewById(R.id.imgFondoSuper);
            nombre = itemView.findViewById(R.id.txtNombreSuper);
            descripcion = itemView.findViewById(R.id.txtDescripcionSuper);
            btnIngresar = itemView.findViewById(R.id.btnIngresar);
        }
    }
}
