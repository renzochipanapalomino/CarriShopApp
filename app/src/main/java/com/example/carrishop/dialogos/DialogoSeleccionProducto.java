package com.example.carrishop.dialogos;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.carrishop.R;
import com.example.carrishop.datos.modelos.ProductoConPrecio;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import java.util.Locale;

/**
 * Diálogo que muestra las variantes de un producto (por marca o tipo)
 * y permite elegir una cantidad antes de agregar al carrito.
 */
public class DialogoSeleccionProducto extends DialogFragment {

    private final List<ProductoConPrecio> variantes;
    private final OnSeleccionListener listener;
    private int indiceSeleccionado = -1;
    private int cantidad = 1;

    public interface OnSeleccionListener {
        void onProductoSeleccionado(ProductoConPrecio producto);
    }

    public DialogoSeleccionProducto(List<ProductoConPrecio> variantes, OnSeleccionListener listener) {
        this.variantes = variantes;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.dialogo_seleccion_producto, container, false);

        RecyclerView recycler = vista.findViewById(R.id.recyclerVariantes);
        TextView txtCantidad = vista.findViewById(R.id.txtCantidad);
        MaterialButton btnMas = vista.findViewById(R.id.btnMas);
        MaterialButton btnMenos = vista.findViewById(R.id.btnMenos);
        MaterialButton btnConfirmar = vista.findViewById(R.id.btnConfirmarSeleccion);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AdaptadorVariante adaptador = new AdaptadorVariante(variantes, pos -> indiceSeleccionado = pos);
        recycler.setAdapter(adaptador);

        btnMas.setOnClickListener(v -> {
            cantidad++;
            txtCantidad.setText(String.valueOf(cantidad));
        });

        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                txtCantidad.setText(String.valueOf(cantidad));
            }
        });

        btnConfirmar.setOnClickListener(v -> {
            if (indiceSeleccionado < 0) {
                txtCantidad.setError("Selecciona un producto antes de continuar");
                return;
            }
            ProductoConPrecio seleccionado = variantes.get(indiceSeleccionado);
            seleccionado.cantidad = cantidad;
            if (listener != null) listener.onProductoSeleccionado(seleccionado);
            dismiss();
        });

        return vista;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null && d.getWindow() != null) {
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    // Adaptador del RecyclerView
    private static class AdaptadorVariante extends RecyclerView.Adapter<AdaptadorVariante.VH> {

        interface OnClickVariante {
            void onClick(int posicion);
        }

        private final List<ProductoConPrecio> data;
        private final OnClickVariante listener;
        private int seleccionado = -1;

        AdaptadorVariante(List<ProductoConPrecio> data, OnClickVariante listener) {
            this.data = data;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_variante_producto, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ProductoConPrecio p = data.get(position);

            String marca = (p.marca == null || p.marca.trim().isEmpty()) ? "Sin marca" : p.marca;
            h.txtNombre.setText(p.nombre);
            h.txtMarca.setText(String.format(Locale.getDefault(), "Marca: %s", marca));
            h.txtPrecio.setText(String.format(Locale.getDefault(), "S/ %.2f", p.precio));

            boolean esSeleccionado = position == seleccionado;
            h.itemView.setSelected(esSeleccionado);
            h.itemView.setAlpha(esSeleccionado ? 1f : 0.8f);

            h.itemView.setOnClickListener(v -> {
                int anterior = seleccionado;
                seleccionado = h.getAdapterPosition();
                if (anterior != -1) notifyItemChanged(anterior);
                notifyItemChanged(seleccionado);
                listener.onClick(seleccionado);
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView txtNombre, txtMarca, txtPrecio;
            VH(@NonNull View v) {
                super(v);
                txtNombre = v.findViewById(R.id.txtNombre);
                txtMarca = v.findViewById(R.id.txtMarca);
                txtPrecio = v.findViewById(R.id.txtPrecio);
            }
        }
    }
}
