package com.example.carrishop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.carrishop.dialogos.FragmentMisEstrellas;
import com.example.carrishop.dialogos.FragmentComoFuncionaPremium;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ActividadMisEstrellas extends AppCompatActivity {

    private final String[] TITULOS_TABS = {"Mis Estrellas", "Cómo Funciona"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_estrellas);

        // 🔹 Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarEstrellas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Carrishop Rewards");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // 🔹 Tabs y ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayoutEstrellas);
        ViewPager2 viewPager = findViewById(R.id.viewPagerEstrellas);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) return new FragmentMisEstrellas();
                else return new FragmentComoFuncionaPremium();
            }

            @Override
            public int getItemCount() {
                return TITULOS_TABS.length;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TITULOS_TABS[position])
        ).attach();
    }
}
