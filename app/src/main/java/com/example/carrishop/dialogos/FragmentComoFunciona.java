package com.example.carrishop.dialogos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carrishop.R;

public class FragmentComoFunciona extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflamos el diseño de "Cómo funciona" (debes tener este XML)
        return inflater.inflate(R.layout.activity_como_funciona_premium, container, false);
    }
}
