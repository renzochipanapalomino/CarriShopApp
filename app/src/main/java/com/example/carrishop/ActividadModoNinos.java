package com.example.carrishop;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActividadModoNinos extends AppCompatActivity {

    private static final long FLOAT_DURATION_MS = 2400L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_ninos);

        TextView titulo = findViewById(R.id.txtTituloKids);
        TextView descripcion = findViewById(R.id.txtDescripcionKids);
        TextView pista = findViewById(R.id.txtHintKids);

        if (titulo != null) {
            titulo.setText(getString(R.string.modo_kids_titulo));
        }
        if (descripcion != null) {
            descripcion.setText(getString(R.string.modo_kids_desc));
        }
        if (pista != null) {
            pista.setText(getString(R.string.modo_kids_hint));
        }

        int[] cardIds = new int[]{
                R.id.cardAnimales,
                R.id.cardNumeros,
                R.id.cardLetras,
                R.id.cardVehiculos,
                R.id.cardJuguetes
        };

        String[] categoryNames = new String[]{
                getString(R.string.modo_kids_category_animals),
                getString(R.string.modo_kids_category_numbers),
                getString(R.string.modo_kids_category_letters),
                getString(R.string.modo_kids_category_vehicles),
                getString(R.string.modo_kids_category_toys)
        };

        for (int i = 0; i < cardIds.length; i++) {
            View card = findViewById(cardIds[i]);
            if (card == null) {
                continue;
            }
            startFloatingAnimation(card, i * 120L);

            final String categoryLabel = categoryNames[i];
            card.setOnClickListener(v -> Toast.makeText(
                    ActividadModoNinos.this,
                    getString(R.string.modo_kids_card_message, categoryLabel),
                    Toast.LENGTH_SHORT
            ).show());
        }
    }

    private void startFloatingAnimation(View card, long delay) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(card, View.TRANSLATION_Y, -12f, 12f);
        animator.setDuration(FLOAT_DURATION_MS);
        animator.setStartDelay(delay);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
}
