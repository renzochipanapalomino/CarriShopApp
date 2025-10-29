package com.example.carrishop.dialogos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.carrishop.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Fragment actualizado: muestra la pestaña "Cómo Funciona"
 * con diseño tipo Starbucks Premium adaptado a Carrishop.
 */
public class FragmentComoFuncionaPremium extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_como_funciona_premium, container, false);

        // 🎬 Animación de aparición para cada bloque
        LinearLayout bloqueAcumula = vista.findViewById(R.id.bloqueAcumula);
        LinearLayout bloqueCanjea = vista.findViewById(R.id.bloqueCanjea);
        LinearLayout bloqueBonus = vista.findViewById(R.id.bloqueBonus);

        if (bloqueAcumula != null)
            bloqueAcumula.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        if (bloqueCanjea != null)
            bloqueCanjea.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        if (bloqueBonus != null)
            bloqueBonus.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

        // ✨ Interacción al tocar bloques
        View.OnClickListener animarBloque = v -> {
            v.animate()
                    .scaleX(0.96f).scaleY(0.96f)
                    .setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150))
                    .start();

            int id = v.getId();
            if (id == R.id.bloqueAcumula) {
                mostrarDialogo(
                        R.drawable.ic_credit_card,
                        "Acumula Estrellas 💫",
                        "Gana estrellas por cada compra mayor a S/300 o usando tu Carrishop Card.\n\n" +
                                "Mientras más compras realices, más beneficios desbloquearás."
                );
            } else if (id == R.id.bloqueCanjea) {
                mostrarDialogo(
                        R.drawable.ic_crown,
                        "Canjea tus Estrellas 🎁",
                        "Usa tus puntos para canjear productos o descuentos exclusivos en Carrishop Premium.\n\n" +
                                "Cada estrella te acerca a nuevas recompensas."
                );
            } else if (id == R.id.bloqueBonus) {
                mostrarDialogo(
                        R.drawable.ic_estrella_llena,
                        "Beneficios Extra Carrishop 💎",
                        "Participa en promociones especiales y gana el doble de estrellas.\n\n" +
                                "Aprovecha eventos semanales y beneficios únicos solo para miembros Premium."
                );
            }
        };

        if (bloqueAcumula != null) bloqueAcumula.setOnClickListener(animarBloque);
        if (bloqueCanjea != null) bloqueCanjea.setOnClickListener(animarBloque);
        if (bloqueBonus != null) bloqueBonus.setOnClickListener(animarBloque);

        // 🔹 Botón inferior “Ver todos los beneficios”
        View btnVerBeneficios = vista.findViewById(R.id.btnVerBeneficios);
        if (btnVerBeneficios != null) {
            btnVerBeneficios.setOnClickListener(v ->
                    mostrarDialogo(
                            R.drawable.ic_crown,
                            "Todos los Beneficios 👑",
                            "Con Carrishop Premium puedes acceder a recompensas exclusivas:\n\n" +
                                    "⭐ Descuentos especiales en supermercados afiliados.\n" +
                                    "🎁 Canje de productos Premium.\n" +
                                    "🏆 Acceso anticipado a promociones y eventos especiales.\n\n" +
                                    "¡Sigue acumulando estrellas y disfruta tu membresía Premium!"
                    ));
        }

        return vista;
    }

    // 🟩 Diálogo moderno
    private void mostrarDialogo(int icono, String titulo, String mensaje) {
        if (getContext() == null) return;

        new MaterialAlertDialogBuilder(requireContext(),
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog)
                .setIcon(icono)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Entendido", (dialog, which) -> dialog.dismiss())
                .setBackground(ResourcesCompat.getDrawable(
                        getResources(), R.drawable.bg_dialog_fondo, null))
                .show();
    }
}
