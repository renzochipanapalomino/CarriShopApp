package com.example.carrishop.dialogos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.MainActivity;
import com.example.carrishop.R;
import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.UsuarioEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;

public class ActividadConfirmacionPago extends AppCompatActivity {

    private ImageView imgCheck;
    private TextView txtMensaje;
    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_pago);

        imgCheck = findViewById(R.id.imgCheck);
        txtMensaje = findViewById(R.id.txtMensajePago);
        btnContinuar = findViewById(R.id.btnContinuarPago);

        String metodo = getIntent().getStringExtra("metodo_pago");
        if (metodo == null || metodo.isEmpty()) {
            txtMensaje.setText("Procesando pago... 💳");
        } else {
            txtMensaje.setText("Procesando pago con " + metodo + "... 💳");
        }
        animarCheck();

        // 🔹 Actualizar estado Premium en BD y sesión
        actualizarCuentaPremium();

        // 🔹 Botón "Continuar"
        btnContinuar.setOnClickListener(v -> irAlInicio());

        // 🔹 Auto cierre después de unos segundos (opcional)
        new Handler().postDelayed(() -> {
            if (!isFinishing()) irAlInicio();
        }, 3500);
    }

    private void animarCheck() {
        imgCheck.setScaleX(0f);
        imgCheck.setScaleY(0f);
        imgCheck.animate().scaleX(1f).scaleY(1f).setDuration(700).start();
    }

    /** 🔸 Marca la cuenta actual como Premium en BD y en las preferencias */
    private void actualizarCuentaPremium() {
        try {
            SesionPrefs prefs = new SesionPrefs(this);

            // 🔹 1. Intentar recuperar el correo del Intent (desde ActividadPagoTarjeta)
            String emailIntent = getIntent().getStringExtra("correo_usuario");

            // 🔹 2. Si no llega por Intent, usar el de la sesión
            String emailActual = (emailIntent != null && !emailIntent.isEmpty())
                    ? emailIntent
                    : prefs.getEmail();

            if (emailActual == null || emailActual.isEmpty()) {
                txtMensaje.setText("Error: no se detectó el correo de la cuenta.");
                return;
            }

            BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
            UsuarioEntidad usuario = db.usuarioDao().porEmail(emailActual);

            if (usuario != null) {
                usuario.esPremium = true;
                db.usuarioDao().insertar(usuario); // reemplaza el registro existente
                prefs.setPremium(true);
                txtMensaje.setText("🎉 ¡Tu cuenta ahora es Premium! Disfruta tus beneficios.");
            } else {
                txtMensaje.setText("Error: usuario no encontrado en la base de datos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            txtMensaje.setText("Error al actualizar el estado Premium.");
        }
    }

    /** 🔸 Redirige al inicio (MainActivity) */
    private void irAlInicio() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
