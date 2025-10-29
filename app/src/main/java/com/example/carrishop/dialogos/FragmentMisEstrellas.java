package com.example.carrishop.dialogos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.carrishop.R;
import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.CanjeEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class FragmentMisEstrellas extends Fragment {

    private MaterialButton btnCanjearEstrellas;
    private SesionPrefs prefs;
    private BaseDeDatosApp db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_mis_estrellas, container, false);

        // Inicializar preferencias y base de datos
        prefs = new SesionPrefs(requireContext());
        db = Room.databaseBuilder(requireContext(), BaseDeDatosApp.class, "carrishop.db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // Vincular botón
        btnCanjearEstrellas = vista.findViewById(R.id.btnCanjearEstrellas);
        btnCanjearEstrellas.setOnClickListener(v -> generarCodigoCanje());

        return vista;
    }

    // 🔹 Genera y guarda un código válido por 24 horas
    private void generarCodigoCanje() {
        String codigo = "CARRI-" + generarAleatorio(6);
        long ahora = System.currentTimeMillis();
        long expiracion = ahora + (24 * 60 * 60 * 1000); // 24h

        CanjeEntidad canje = new CanjeEntidad();
        canje.idUsuario = 1; // ⚠️ Temporal hasta conectar SesionPrefs con login real
        canje.producto = "Pan de Molde Familiar";
        canje.codigo = codigo;
        canje.fechaGeneracion = formatoFecha(ahora);
        canje.fechaExpiracion = formatoFecha(expiracion);

        db.canjeDao().insertar(canje);

        mostrarDialogoCodigo(codigo, expiracion);
    }

    // 🔹 Genera una cadena aleatoria de letras y números
    private String generarAleatorio(int longitud) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < longitud; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 🔹 Formatea la fecha legible
    private String formatoFecha(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(millis));
    }

    // 🔹 Muestra el código generado en un diálogo
    private void mostrarDialogoCodigo(String codigo, long expiracionMillis) {
        String fechaExpira = formatoFecha(expiracionMillis);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("🎁 Código de Canje");
        builder.setMessage(
                "\n🪄 Tu código:\n\n" +
                        "👉 " + codigo + "\n\n" +
                        "📅 Válido hasta: " + fechaExpira + "\n\n" +
                        "Dicta o muestra este código al cajero."
        );
        builder.setPositiveButton("Listo", null);
        builder.show();
    }
}
