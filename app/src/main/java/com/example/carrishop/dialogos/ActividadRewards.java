package com.example.carrishop.dialogos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carrishop.R;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ActividadRewards extends AppCompatActivity {

    private SesionPrefs prefs;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private RewardsPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        prefs = new SesionPrefs(this);

        // 🔒 Verificar si es Premium
        if (!prefs.isPremium()) {
            Toast.makeText(this, "Acceso exclusivo para usuarios Premium 💎", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tabLayout = findViewById(R.id.tabLayoutRewards);
        viewPager = findViewById(R.id.viewPagerRewards);

        // Adaptador del ViewPager2
        adapter = new RewardsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Conectar pestañas con ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Mis Estrellas");
            } else {
                tab.setText("Cómo Funciona");
            }
        }).attach();
    }
}
