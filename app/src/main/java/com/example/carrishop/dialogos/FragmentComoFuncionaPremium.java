package com.example.carrishop.dialogos;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.carrishop.R;
import com.google.android.material.button.MaterialButton;
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
            btnVerBeneficios.setOnClickListener(v -> mostrarDialogoBeneficios());
        }

        return vista;
    }

    // 🟩 Diálogo moderno para bloques
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

    private void mostrarDialogoBeneficios() {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_beneficios_premium);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View beneficioUno = dialog.findViewById(R.id.beneficioUno);
        View beneficioDos = dialog.findViewById(R.id.beneficioDos);
        View beneficioTres = dialog.findViewById(R.id.beneficioTres);

        configurarBeneficio(beneficioUno, R.drawable.ic_estrella_llena,
                "Estrellas dobles", "Acumula el doble los fines de semana Premium.");
        configurarBeneficio(beneficioDos, R.drawable.ic_credit_card,
                "Checkout prioritario", "Recibe atención exclusiva en cajas Carrishop.");
        configurarBeneficio(beneficioTres, R.drawable.ic_crown,
                "Experiencias Rewards", "Accede a catas, workshops y lanzamientos privados.");

        ImageButton cerrar = dialog.findViewById(R.id.btnCerrarBeneficios);
        MaterialButton listo = dialog.findViewById(R.id.btnEntendidoBeneficios);

        if (cerrar != null) cerrar.setOnClickListener(v -> dialog.dismiss());
        if (listo != null) listo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void configurarBeneficio(View contenedor, int icono, String titulo, String descripcion) {
        if (contenedor == null) return;
        ImageView iconView = contenedor.findViewById(R.id.imgIconoBeneficio);
        TextView tituloView = contenedor.findViewById(R.id.txtTituloBeneficio);
        TextView descripcionView = contenedor.findViewById(R.id.txtDescripcionBeneficio);

        if (iconView != null) iconView.setImageResource(icono);
        if (tituloView != null) tituloView.setText(titulo);
        if (descripcionView != null) descripcionView.setText(descripcion);
    }
}
