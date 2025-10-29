package com.example.carrishop.dialogos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RewardsPagerAdapter extends FragmentStateAdapter {

    public RewardsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FragmentMisEstrellas();
        } else {
            return new FragmentComoFunciona();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
