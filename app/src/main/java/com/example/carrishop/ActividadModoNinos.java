package com.example.carrishop;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActividadModoNinos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_ninos);

        ImageView icon = findViewById(R.id.imgSync);
        TextView texto = findViewById(R.id.txtSync);

        texto.setText("Sincronización exitosa ✅");

        // 🔹 Cierra la ventana automáticamente en 2 segundos
        new Handler().postDelayed(this::finish, 2000);
    }
}
