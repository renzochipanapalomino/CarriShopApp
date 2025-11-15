package com.example.carrishop.dialogos;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.carrishop.R;
import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.CanjeEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;

public class FragmentMisEstrellas extends Fragment {

    private final int[] niveles = {25, 50, 100, 200, 300};
    private final RewardItem[] catalogo = new RewardItem[]{
            new RewardItem(R.drawable.prod_pan, "Pan artesanal integral", 25,
                    "Desayunos suaves con aroma recién horneado."),
            new RewardItem(R.drawable.prod_cafe, "Café premium molido", 50,
                    "Mejora tus mañanas con granos seleccionados."),
            new RewardItem(R.drawable.prod_leche_gloria, "Box desayuno energético", 100,
                    "Incluye lácteos y cereales para compartir."),
            new RewardItem(R.drawable.prod_aceite, "Set gourmet de cocina", 200,
                    "Aceites y condimentos para recetas especiales."),
            new RewardItem(R.drawable.prod_gaseosa, "Celebración familiar", 300,
                    "Arma tu mesa con bebidas y snacks premium.")
    };

    private MaterialButton btnCanjearEstrellas;
    private SesionPrefs prefs;
    private BaseDeDatosApp db;
    private ImageView imgRecompensa;
    private TextView txtSaludoUsuario;
    private TextView txtTotalEstrellas;
    private TextView txtTituloRecompensa;
    private TextView txtDescripcionRecompensa;
    private TextView[] chipsNivel;
    private RewardItem recompensaActual;
    private CountDownTimer countDownTimer;
    private final Random random = new Random();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_mis_estrellas, container, false);

        prefs = new SesionPrefs(requireContext());
        db = Room.databaseBuilder(requireContext(), BaseDeDatosApp.class, "carrishop.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        btnCanjearEstrellas = vista.findViewById(R.id.btnCanjearEstrellas);
        imgRecompensa = vista.findViewById(R.id.imgRecompensa);
        txtSaludoUsuario = vista.findViewById(R.id.txtSaludoUsuario);
        txtTotalEstrellas = vista.findViewById(R.id.txtTotalEstrellas);
        txtTituloRecompensa = vista.findViewById(R.id.txtTituloRecompensa);
        txtDescripcionRecompensa = vista.findViewById(R.id.txtDescripcionRecompensa);

        chipsNivel = new TextView[]{
                vista.findViewById(R.id.txtNivel25),
                vista.findViewById(R.id.txtNivel50),
                vista.findViewById(R.id.txtNivel100),
                vista.findViewById(R.id.txtNivel200),
                vista.findViewById(R.id.txtNivel300)
        };

        btnCanjearEstrellas.setOnClickListener(v -> generarCodigoCanje());

        actualizarDatosUsuario();
        mostrarRecompensaAleatoria();
        resaltarNiveles(prefs.getPuntos());

        return vista;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detenerCuentaRegresiva();
    }

    private void actualizarDatosUsuario() {
        String nombre = prefs.getNombre();
        int puntos = prefs.getPuntos();

        if (txtSaludoUsuario != null) {
            txtSaludoUsuario.setText(getString(R.string.mis_estrellas_saludo, nombre));
        }
        if (txtTotalEstrellas != null) {
            txtTotalEstrellas.setText(getString(R.string.mis_estrellas_total, puntos));
        }
    }

    private void mostrarRecompensaAleatoria() {
        recompensaActual = catalogo[random.nextInt(catalogo.length)];

        if (imgRecompensa != null) {
            imgRecompensa.setImageResource(recompensaActual.drawableRes);
        }
        if (txtTituloRecompensa != null) {
            txtTituloRecompensa.setText(recompensaActual.titulo);
        }
        if (txtDescripcionRecompensa != null) {
            String detalle = recompensaActual.descripcion + "\n" +
                    getString(R.string.mis_estrellas_recompensa, recompensaActual.puntos);
            txtDescripcionRecompensa.setText(detalle);
        }
    }

    private void resaltarNiveles(int puntosUsuario) {
        if (chipsNivel == null || getContext() == null) return;

        for (int i = 0; i < chipsNivel.length; i++) {
            TextView chip = chipsNivel[i];
            if (chip == null) continue;
            boolean activo = puntosUsuario >= niveles[i];
            chip.setBackgroundResource(activo ? R.drawable.bg_nivel_activo : R.drawable.bg_nivel_inactivo);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    activo ? R.color.white : R.color.texto));
        }
    }

    private void generarCodigoCanje() {
        String codigo = generarCodigoNumerico(6);
        long ahora = System.currentTimeMillis();
        long expiracion = ahora + (24 * 60 * 60 * 1000);

        CanjeEntidad canje = new CanjeEntidad();
        int usuarioId = prefs.obtenerUsuarioId();
        canje.idUsuario = usuarioId == 0 ? 1 : usuarioId;
        canje.producto = recompensaActual != null ? recompensaActual.titulo : "Recompensa Premium";
        canje.codigo = codigo;
        canje.fechaGeneracion = formatoFecha(ahora);
        canje.fechaExpiracion = formatoFecha(expiracion);

        db.canjeDao().insertar(canje);

        mostrarDialogoCodigo(codigo, 120_000L);
    }

    private String generarCodigoNumerico(int longitud) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String formatoFecha(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(millis));
    }

    private void mostrarDialogoCodigo(String codigo, long duracionMillis) {
        if (getContext() == null) return;

        View contenido = LayoutInflater.from(getContext()).inflate(R.layout.dialog_codigo_canje, null, false);
        TextView txtCodigo = contenido.findViewById(R.id.txtCodigoGenerado);
        TextView txtTiempo = contenido.findViewById(R.id.txtTiempoCodigo);
        ProgressBar progressBar = contenido.findViewById(R.id.progressCodigo);
        MaterialButton btnCopiar = contenido.findViewById(R.id.btnCopiarCodigo);
        ImageButton btnCerrar = contenido.findViewById(R.id.btnCerrarCodigo);

        txtCodigo.setText(formatearCodigo(codigo));
        progressBar.setMax((int) (duracionMillis / 1000));
        progressBar.setProgress(progressBar.getMax());
        txtTiempo.setText(getString(R.string.codigo_restante_placeholder, duracionMillis / 1000));

        AlertDialog dialogo = new MaterialAlertDialogBuilder(requireContext())
                .setView(contenido)
                .create();

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialogo.setCanceledOnTouchOutside(false);
        dialogo.show();

        iniciarCuentaRegresiva(duracionMillis, txtTiempo, progressBar);

        btnCopiar.setOnClickListener(v -> copiarCodigoAlPortapapeles(codigo));
        btnCerrar.setOnClickListener(v -> dialogo.dismiss());
        dialogo.setOnDismissListener(d -> detenerCuentaRegresiva());
    }

    private void iniciarCuentaRegresiva(long duracion, TextView txtTiempo, ProgressBar progressBar) {
        detenerCuentaRegresiva();
        countDownTimer = new CountDownTimer(duracion, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long segundos = millisUntilFinished / 1000;
                txtTiempo.setText(getString(R.string.codigo_restante_placeholder, segundos));
                progressBar.setProgress((int) segundos);
            }

            @Override
            public void onFinish() {
                txtTiempo.setText(getString(R.string.codigo_restante_placeholder, 0));
                progressBar.setProgress(0);
            }
        };
        countDownTimer.start();
    }

    private void detenerCuentaRegresiva() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void copiarCodigoAlPortapapeles(String codigo) {
        if (getContext() == null) return;
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("codigo_canje", codigo));
            Toast.makeText(requireContext(), getString(R.string.codigo_copiado), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatearCodigo(String codigo) {
        if (codigo.length() <= 3) return codigo;
        return codigo.substring(0, 3) + " " + codigo.substring(3);
    }

    private static class RewardItem {
        final int drawableRes;
        final String titulo;
        final int puntos;
        final String descripcion;

        RewardItem(int drawableRes, String titulo, int puntos, String descripcion) {
            this.drawableRes = drawableRes;
            this.titulo = titulo;
            this.puntos = puntos;
            this.descripcion = descripcion;
        }
    }
}
