package com.example.carrishop;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.adaptadores.SupermercadoAdaptador;
import com.example.carrishop.datos.modelos.SupermercadoModelo;
import com.example.carrishop.datos.prefs.SesionPrefs;

import java.util.ArrayList;
import java.util.List;

public class ActividadSupermercados extends AppCompatActivity {

    private SesionPrefs prefs;
    private ProgressBar barraProgreso;
    private TextView txtPuntos, txtTituloPuntos;
    private LinearLayout encabezadoPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermercados);

        prefs = new SesionPrefs(this);

        // =========================================================
        // 🧍 Recuperar nombre y correo del usuario
        // =========================================================
        String nombre = getIntent().getStringExtra("nombre");
        String email = getIntent().getStringExtra("email");
        if (nombre == null || nombre.trim().isEmpty()) nombre = prefs.getNombre();
        if (email == null || email.trim().isEmpty()) email = prefs.getEmail();
        if (nombre == null || nombre.trim().isEmpty()) nombre = "Usuario";

        // 👋 Saludo
        TextView tvBienvenido = findViewById(R.id.txtBienvenido);
        if (tvBienvenido != null)
            tvBienvenido.setText("Bienvenido, " + nombre);

        // =========================================================
        // 💎 Encabezado Premium
        // =========================================================
        encabezadoPremium = findViewById(R.id.encabezadoPremium);
        txtPuntos = findViewById(R.id.txtPuntosActuales);
        txtTituloPuntos = findViewById(R.id.txtTituloPuntos);
        barraProgreso = findViewById(R.id.barraProgresoPuntos);
        Button btnVerificar = findViewById(R.id.btnVerificarPuntos);
        TextView txtDescripcionBasico = findViewById(R.id.txtDescripcionBasico);
        Button btnPremium = findViewById(R.id.btnPremium);
        View lineaDivisoria = findViewById(R.id.lineaDivisoria);

        // =============================
        // 🟢 Usuario Premium
        // =============================
        if (prefs.isPremium()) {
            encabezadoPremium.setVisibility(View.VISIBLE);
            txtDescripcionBasico.setVisibility(View.GONE);
            btnPremium.setVisibility(View.GONE);
            if (lineaDivisoria != null) lineaDivisoria.setVisibility(View.VISIBLE);

            actualizarBarraPuntos();

            btnVerificar.setOnClickListener(v -> {
                Intent intent = new Intent(this, ActividadMisEstrellas.class);
                startActivity(intent);
            });

        } else {
            // =============================
            // 🔵 Usuario Básico
            // =============================
            encabezadoPremium.setVisibility(View.GONE);
            txtDescripcionBasico.setVisibility(View.VISIBLE);
            btnPremium.setVisibility(View.VISIBLE);

            btnPremium.setOnClickListener(v -> {
                // ➡️ Redirige al proceso de pago
                Intent intent = new Intent(this, com.example.carrishop.dialogos.ActividadPagoTarjeta.class);
                intent.putExtra("total", 9.90); // 💰 Monto del plan Premium
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }

        // =========================================================
        // 🏪 Lista de supermercados
        // =========================================================
        RecyclerView rv = findViewById(R.id.listaSupermercados);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new EspaciadoVertical(dp(12)));

        List<SupermercadoModelo> data = new ArrayList<>();
        data.add(new SupermercadoModelo(
                1, "Tottus", R.drawable.logo_totus,
                "Tottus es una cadena peruana de supermercados del grupo Falabella. Ofertas constantes y productos frescos."
        ));
        data.add(new SupermercadoModelo(
                2, "Plaza Vea", R.drawable.logo_plaza_vea,
                "Plaza Vea, del grupo Intercorp, destaca por sus precios bajos garantizados y su delivery rápido."
        ));
        data.add(new SupermercadoModelo(
                3, "Saga Falabella", R.drawable.logo_saga_falabella,
                "Saga Falabella, líder en moda, hogar y tecnología. Parte del grupo Falabella."
        ));
        data.add(new SupermercadoModelo(
                4, "Ripley", R.drawable.logo_ripley,
                "Ripley ofrece moda, tecnología y hogar con beneficios exclusivos de su programa de puntos."
        ));
        data.add(new SupermercadoModelo(
                5, "Mass", R.drawable.logo_mass,
                "Mass, del grupo Intercorp, es la tienda cercana de productos esenciales y precios accesibles."
        ));

        String finalNombre = nombre;
        String finalEmail = email;

        SupermercadoAdaptador ad = new SupermercadoAdaptador(data, item -> {
            Intent i = new Intent(this, ActividadEscanearVoz.class);
            i.putExtra("superId", item.id);
            i.putExtra("superNombre", item.nombre);
            i.putExtra("nombre", finalNombre);
            i.putExtra("email", finalEmail);
            startActivity(i);
        });
        rv.setAdapter(ad);

        // 🚪 Botón “Salir”
        findViewById(R.id.btnSalir).setOnClickListener(v -> {
            Intent intent = new Intent(this, ActividadBienvenida.class);
            intent.putExtra("nombre", prefs.getNombre());
            intent.putExtra("email", prefs.getEmail());
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 🔄 Si el usuario ya se volvió Premium luego de pagar, se actualiza el panel
        if (prefs.isPremium()) {
            LinearLayout encabezadoPremium = findViewById(R.id.encabezadoPremium);
            TextView txtDescripcionBasico = findViewById(R.id.txtDescripcionBasico);
            Button btnPremium = findViewById(R.id.btnPremium);

            encabezadoPremium.setVisibility(View.VISIBLE);
            txtDescripcionBasico.setVisibility(View.GONE);
            btnPremium.setVisibility(View.GONE);

            actualizarBarraPuntos();
        }
    }

    // =========================================================
    // ⭐ MÉTODO: Actualizar barra, color y nivel
    // =========================================================
    private void actualizarBarraPuntos() {
        int puntos = prefs.getPuntos();
        txtPuntos.setText(puntos + " pts");

        int color;
        String nivelTexto;
        if (puntos < 100) {
            color = ContextCompat.getColor(this, R.color.primario);
            nivelTexto = "Nivel: Bronce 🥉";
        } else if (puntos < 200) {
            color = ContextCompat.getColor(this, R.color.verde_plata);
            nivelTexto = "Nivel: Plata 🥈";
        } else if (puntos < 300) {
            color = ContextCompat.getColor(this, R.color.oro);
            nivelTexto = "Nivel: Oro 🥇";
        } else {
            color = ContextCompat.getColor(this, R.color.fucsia_diamante);
            nivelTexto = "Nivel: Diamante 💎";
        }

        txtTituloPuntos.setText("⭐ Estrellas acumuladas\n" + nivelTexto);

        int progresoActual = barraProgreso.getProgress();
        int nuevoProgreso = Math.min(puntos, 300);

        ObjectAnimator anim = ObjectAnimator.ofInt(barraProgreso, "progress", progresoActual, nuevoProgreso);
        anim.setDuration(800);
        anim.start();

        barraProgreso.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    // =========================================================
    // 🔸 Conversión dp → píxeles
    // =========================================================
    private int dp(float v) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, v, Resources.getSystem().getDisplayMetrics()));
    }

    // =========================================================
    // 🔸 Espaciado vertical en RecyclerView
    // =========================================================
    static class EspaciadoVertical extends RecyclerView.ItemDecoration {
        private final int espacio;

        EspaciadoVertical(int espacio) {
            this.espacio = espacio;
        }

        @Override
        public void getItemOffsets(@NonNull android.graphics.Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.top = espacio;
            outRect.bottom = espacio;
        }
    }
}
