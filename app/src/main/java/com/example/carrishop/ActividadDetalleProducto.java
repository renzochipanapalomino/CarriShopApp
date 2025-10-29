package com.example.carrishop;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Muestra los detalles del producto con animaciones y estilo limpio.
 */
public class ActividadDetalleProducto extends AppCompatActivity {

    private String nombre;
    private double precio;
    private int imagenRes;
    private String descripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        ImageView img = findViewById(R.id.imgDetalle);
        TextView txtNombre = findViewById(R.id.txtNombreDetalle);
        TextView txtPrecio = findViewById(R.id.txtPrecioDetalle);
        TextView txtDesc = findViewById(R.id.txtDescripcionDetalle);
        MaterialButton btnAgregar = findViewById(R.id.btnAgregarCarritoDetalle);
        MaterialButton btnSalir = findViewById(R.id.btnVolverDetalle);

        nombre = getIntent().getStringExtra("nombre");
        precio = getIntent().getDoubleExtra("precio", 0.0);
        imagenRes = getIntent().getIntExtra("imagen", R.drawable.ic_image_placeholder);
        descripcion = getIntent().getStringExtra("descripcion");

        img.setImageDrawable(ContextCompat.getDrawable(this, imagenRes));
        txtNombre.setText(nombre);
        txtPrecio.setText(String.format("S/ %.2f", precio));
        txtDesc.setText(descripcion != null ? descripcion :
                "Producto disponible con beneficios exclusivos.");

        btnAgregar.setOnClickListener(v -> {
            SharedPreferences sp = getSharedPreferences("carrito", Context.MODE_PRIVATE);
            var set = new java.util.HashSet<>(sp.getStringSet("items", new java.util.HashSet<>()));
            set.add(nombre + "|" + precio + "|" + imagenRes);
            sp.edit().putStringSet("items", set).apply();

            animarBoton(btnAgregar);
            Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();
        });

        btnSalir.setOnClickListener(v -> finish());
    }

    private void animarBoton(MaterialButton btn) {
        int colorStart = ContextCompat.getColor(this, R.color.exito);
        int colorEnd = ContextCompat.getColor(this, R.color.primario);
        ValueAnimator anim = ObjectAnimator.ofInt(btn, "backgroundTint", colorStart, colorEnd);
        anim.setEvaluator(new ArgbEvaluator());
        anim.setDuration(400);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim.start();
    }
}
