package com.example.carrishop.dialogos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.R;
import com.example.carrishop.datos.prefs.SesionPrefs;

public class ActividadPremium extends AppCompatActivity {

    private Button btnIrAlPago;
    private ImageView imgPremium;
    private TextView txtDescripcion;
    private SesionPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        prefs = new SesionPrefs(this);

        imgPremium = findViewById(R.id.imgPremium);
        txtDescripcion = findViewById(R.id.txtDescripcionPremium);
        btnIrAlPago = findViewById(R.id.btnIrAlPago);

        // 🔹 Descripción del plan Premium
        txtDescripcion.setText(
                "Con CarriShop Premium obtienes beneficios exclusivos:\n\n" +
                        " • Guarda tus boletas como imágenes.\n" +
                        " • Visualiza estadísticas de gasto semanal y mensual.\n" +
                        " • Acumula puntos con cada compra.\n\n" +
                        "Haz tu compra inteligente con CarriShop Premium."
        );

        // 🔹 Validar si ya es Premium
        if (prefs.isPremium()) {
            Toast.makeText(this, "⭐ Tu cuenta ya es PREMIUM. ¡Disfruta tus beneficios!", Toast.LENGTH_LONG).show();

            // Redirigir directamente a la interfaz Premium
            Intent intent = new Intent(this, com.example.carrishop.ActividadSupermercados.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // 🔹 Botón para proceder al checkout
        btnIrAlPago.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActividadCheckout.class);
            intent.putExtra("total", 9.90);
            startActivity(intent);
        });
    }
}
