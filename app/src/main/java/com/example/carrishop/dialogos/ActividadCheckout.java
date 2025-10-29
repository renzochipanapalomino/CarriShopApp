package com.example.carrishop.dialogos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.R;
import com.example.carrishop.datos.prefs.SesionPrefs;

public class ActividadCheckout extends AppCompatActivity {

    private LinearLayout opcionTarjeta;
    private TextView txtTotal;
    private double total = 0;
    private SesionPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        opcionTarjeta = findViewById(R.id.opcionTarjeta);
        txtTotal = findViewById(R.id.txtTotalCheckout);
        prefs = new SesionPrefs(this);

        // 🔹 Recibir total desde Premium
        total = getIntent().getDoubleExtra("total", 9.90);
        txtTotal.setText("Total: PEN " + String.format("%.2f", total));

        // 🔹 Si el usuario ya es Premium, evitar nueva compra
        if (prefs.isPremium()) {
            Toast.makeText(this, "⚠️ Tu cuenta ya es PREMIUM. No es necesario volver a pagar.", Toast.LENGTH_LONG).show();

            // Redirigir al inicio Premium
            Intent intent = new Intent(this, com.example.carrishop.ActividadSupermercados.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // 🔹 Clic en tarjeta → ir a pago
        opcionTarjeta.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActividadPagoTarjeta.class);
            intent.putExtra("total", total);
            startActivity(intent);
        });
    }
}
