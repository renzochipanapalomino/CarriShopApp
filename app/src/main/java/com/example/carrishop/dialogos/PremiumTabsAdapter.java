package com.example.carrishop.dialogos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PremiumTabsAdapter extends FragmentStateAdapter {

    public PremiumTabsAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FragmentMisEstrellas(); // ya creado en paso anterior
        } else {
            return new FragmentComoFuncionaPremium(); // lo haremos aquí
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
