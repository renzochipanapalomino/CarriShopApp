package com.example.carrishop;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.CompraEntidad;
import com.example.carrishop.datos.modelos.ProductoCapturado;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.google.android.material.button.MaterialButton;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 🧾 Pantalla de resumen de compra (Premium/Básico)
 * Permite:
 * - Ver resumen de productos
 * - Guardar como imagen (boleta completa)
 * - Registrar compra en BD
 * - Ver control de compras (solo Premium)
 */
public class ActividadResumenCompra extends AppCompatActivity {

    private SesionPrefs prefs;
    private BaseDeDatosApp db;
    private NestedScrollView scrollResumen;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_resumen_compra);

        prefs = new SesionPrefs(this);
        db = BaseDeDatosApp.obtener(this);
        scrollResumen = findViewById(R.id.scroll);

        // =========================================================
        // 👤 DATOS DEL USUARIO
        // =========================================================
        String nombre = getIntent().getStringExtra("nombre");
        String correo = getIntent().getStringExtra("email");
        String superNombre = getIntent().getStringExtra("superNombre");

        if (nombre == null || nombre.trim().isEmpty()) nombre = prefs.getNombre();
        if (correo == null || correo.trim().isEmpty()) correo = prefs.getEmail();
        if (nombre == null || nombre.trim().isEmpty()) nombre = "Usuario";

        TextView txtNombreUser = findViewById(R.id.txtNombreUser);
        TextView txtEmailUser = findViewById(R.id.txtEmailUser);
        TextView txtRolUser = findViewById(R.id.txtRolUser);

        txtNombreUser.setText(nombre);
        txtEmailUser.setText(correo.isEmpty() ? "usuario@correo.com" : correo);
        txtRolUser.setText(
                (superNombre == null || superNombre.isEmpty())
                        ? ""
                        : "Comprando en " + superNombre
        );

        // =========================================================
        // 🛒 LISTA DE PRODUCTOS
        // =========================================================
        ArrayList<ProductoCapturado> items =
                (ArrayList<ProductoCapturado>) getIntent().getSerializableExtra("items");
        if (items == null) items = new ArrayList<>();

        RecyclerView rv = findViewById(R.id.recyclerResumen);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ResumenProductoAdapter(items));

        // =========================================================
        // 💰 CALCULAR TOTAL
        // =========================================================
        double totalTmp = 0;
        for (ProductoCapturado p : items) {
            int cantidad = (p.cantidad > 0) ? p.cantidad : 1;
            totalTmp += (p.precio * cantidad);
        }

        final double total = totalTmp;
        final ArrayList<ProductoCapturado> finalItems = items;
        final String finalNombre = nombre;
        final String finalCorreo = correo;

        TextView txtTotal = findViewById(R.id.txtTotal);
        txtTotal.setText(String.format(Locale.US, "Total: S/ %.2f", total));

        // =========================================================
        // 🧾 BOTÓN GUARDAR COMO IMAGEN (solo Premium)
        // =========================================================
        MaterialButton btnGuardar = findViewById(R.id.btnGuardarImagen);
        btnGuardar.setOnClickListener(v -> {
            if (prefs.isPremium()) {
                guardarScrollCompletoComoImagen(scrollResumen);
            } else {
                Toast.makeText(this,
                        "Disponible solo para usuarios Premium 💎",
                        Toast.LENGTH_LONG).show();
            }
        });

        // =========================================================
        // ✅ BOTÓN FINALIZAR COMPRA
        // =========================================================
        MaterialButton btnFinalizar = findViewById(R.id.btnFinalizar);
        btnFinalizar.setOnClickListener(v -> {
            Toast.makeText(this, "¡Compra finalizada!", Toast.LENGTH_SHORT).show();

            if (prefs.isPremium()) {
                registrarCompra(db, prefs.obtenerUsuarioId(), total, finalItems.size());

                if (total >= 200) {
                    prefs.addPuntos(25);
                    int nuevos = prefs.getPuntos();
                    Toast.makeText(this,
                            "🎖️ ¡Felicitaciones, " + finalNombre +
                                    "! Has ganado 25 puntos. Total acumulado: " + nuevos,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this,
                        "Compra registrada (modo básico). Actualiza a Premium para más beneficios.",
                        Toast.LENGTH_LONG).show();
            }

            // 🧹 LIMPIAR EL CARRITO DESPUÉS DE FINALIZAR COMPRA
            ActividadEscanearVoz.limpiarCarrito(this);

            // 🔁 Volver a la pantalla principal
            Intent intent = new Intent(this, ActividadBienvenida.class);
            intent.putExtra("nombre", finalNombre);
            intent.putExtra("email", finalCorreo);
            startActivity(intent);
            finish();
        });

        // =========================================================
        // 🔁 BOTÓN VOLVER A ESCANEAR
        // =========================================================
        MaterialButton btnVolver = findViewById(R.id.btnVolverScan);
        btnVolver.setOnClickListener(v -> finish());

        // =========================================================
        // 📊 BOTÓN CONTROL DE COMPRAS (solo Premium)
        // =========================================================
        MaterialButton btnControl = findViewById(R.id.btnControlCompras);
        if (prefs.isPremium()) {
            btnControl.setVisibility(View.VISIBLE);
            btnControl.setOnClickListener(v -> {
                Intent intent = new Intent(this, ActividadControlCompras.class);
                startActivity(intent);
            });
        } else {
            btnControl.setVisibility(View.GONE);
        }
    }

    // =========================================================
    // 💾 REGISTRAR COMPRA EN BASE DE DATOS
    // =========================================================
    private void registrarCompra(BaseDeDatosApp db, int idUsuario, double total, int cantidad) {
        CompraEntidad c = new CompraEntidad();
        c.idUsuario = idUsuario;
        c.fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        c.total = total;
        c.cantidadProductos = cantidad;
        db.compraDao().insertar(c);
    }

    // =========================================================
    // 📸 GUARDAR TODO EL SCROLL COMO IMAGEN COMPLETA
    // =========================================================
    private void guardarScrollCompletoComoImagen(View view) {
        try {
            // Si el view es un NestedScrollView, tomamos su contenido completo
            View target = view;
            if (view instanceof NestedScrollView) {
                target = ((NestedScrollView) view).getChildAt(0);
            }

            int totalHeight = target.getHeight();
            int totalWidth = target.getWidth();

            Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            target.draw(canvas);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                    "boleta_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CarriShopBoletas");

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    Toast.makeText(this, "🧾 Boleta completa guardada en la galería 📸", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar la boleta", Toast.LENGTH_SHORT).show();
        }
    }
}
