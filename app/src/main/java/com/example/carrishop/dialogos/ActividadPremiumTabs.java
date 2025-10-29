package com.example.carrishop.dialogos;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carrishop.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Controla las pestañas del módulo Carrishop Rewards.
 * Muestra las secciones "Mis Estrellas" y "Cómo Funciona"
 * con diseño limpio, línea divisoria, banner animado y botón de retroceso.
 */
public class ActividadPremiumTabs extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private PremiumTabsAdapter adapter;
    private View bannerPremium;
    private View dividerToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_tabs);

        // ==============================
        // 🎯 CONFIGURAR TOOLBAR SUPERIOR
        // ==============================
        toolbar = findViewById(R.id.toolbarPremiumTabs);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Carrishop Rewards");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Acción de retroceso
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ==============================
        // 🌟 CONFIGURAR ELEMENTOS DE UI
        // ==============================
        tabLayout = findViewById(R.id.tabLayoutPremium);
        viewPager = findViewById(R.id.viewPagerPremium);
        bannerPremium = findViewById(R.id.bannerPremium);
        dividerToolbar = findViewById(R.id.dividerToolbar);

        // Mostrar línea divisoria bajo el encabezado
        if (dividerToolbar != null) dividerToolbar.setVisibility(View.VISIBLE);

        // ==============================
        // ⭐ CONFIGURAR ADAPTADOR Y TABS
        // ==============================
        adapter = new PremiumTabsAdapter(this);
        viewPager.setAdapter(adapter);

        // Animación de transición entre pestañas
        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(1 - Math.abs(position * 0.3f));
            page.setTranslationX(-position * page.getWidth() * 0.3f);
        });

        // Vincular pestañas con el ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Mis Estrellas");
                    break;
                case 1:
                    tab.setText("Cómo Funciona");
                    break;
            }
        }).attach();

        // ==============================
        // 💚 ANIMACIÓN DEL BANNER VERDE
        // ==============================
        if (bannerPremium != null) {
            // Aparece suavemente
            Animation aparecer = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            bannerPremium.startAnimation(aparecer);

            // ⏳ Desaparece automáticamente después de 60 segundos
            new Handler().postDelayed(() -> {
                Animation desaparecer = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                bannerPremium.startAnimation(desaparecer);
                bannerPremium.setVisibility(View.GONE);
            }, 60000); // 1 minuto (puedes cambiar a 10000 = 10 seg para probar)
        }
    }
}
