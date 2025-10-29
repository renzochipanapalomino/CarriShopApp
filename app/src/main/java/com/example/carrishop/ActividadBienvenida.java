package com.example.carrishop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.UsuarioEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.example.carrishop.datos.util.BackupManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ActividadBienvenida extends AppCompatActivity {

    private String nombre = "", email = "";
    private ActivityResultLauncher<Intent> perfilLauncher;
    private SesionPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        prefs = new SesionPrefs(this);

        // 🔹 Restaurar usuario si BD vacía
        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
        if (db.usuarioDao().porEmail(prefs.getEmail()) == null) {
            UsuarioEntidad u = BackupManager.restaurar(this);
            if (u != null) {
                prefs.saveFull(u.nombre, u.email, u.contrasena, prefs.isRecordar());
                prefs.setPremium(u.esPremium);
                nombre = (u.nombre != null && !u.nombre.trim().isEmpty()) ? u.nombre.trim() : "Usuario";
                email = u.email;
                Toast.makeText(this, "Cuenta restaurada automáticamente", Toast.LENGTH_SHORT).show();
            }
        }

        // 🔹 Mostrar animación
        ImageView img = findViewById(R.id.imgBienvenida);
        Glide.with(this)
                .asGif()
                .load(R.drawable.bien)
                .centerInside()
                .into(img);

        // 🔹 Recuperar datos guardados
        nombre = prefs.getNombre();
        email = prefs.getEmail();

        // 🔹 Actualizar si viene desde login
        if (getIntent() != null) {
            String nIntent = getIntent().getStringExtra("nombre");
            String eIntent = getIntent().getStringExtra("email");
            boolean esPremium = getIntent().getBooleanExtra("esPremium", false);

            if (nIntent != null && !nIntent.isEmpty()) nombre = nIntent;
            if (eIntent != null && !eIntent.isEmpty()) email = eIntent;
            if (esPremium) {
                prefs.setPremium(true);
                Toast.makeText(this, "Cuenta Premium activa 💎", Toast.LENGTH_SHORT).show();
            }
        }

        if (nombre == null || nombre.trim().isEmpty()) nombre = "Usuario";

        // 🔹 Mostrar textos
        TextView titulo = findViewById(R.id.txtTituloBienvenida);
        TextView titulo2 = findViewById(R.id.txtNombreBienvenida);
        titulo.setText("BIENVENIDO");
        titulo2.setText(nombre.toUpperCase());

        // 🔹 Configurar Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarBienvenida);
        toolbar.setNavigationOnClickListener(v -> mostrarSheetPerfil());
        toolbar.setOnMenuItemClickListener(this::onToolbarItem);
        toolbar.post(() -> {
            MenuItem salir = toolbar.getMenu().findItem(R.id.action_salir);
            if (salir != null && salir.getIcon() != null) {
                salir.getIcon().setTint(ContextCompat.getColor(this, android.R.color.white));
            }
        });

        // 🔹 Recibir cambios desde Perfil
        perfilLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        String nuevoNombre = data.getStringExtra("nombre");
                        String nuevoEmail = data.getStringExtra("email");
                        if (nuevoNombre != null) nombre = nuevoNombre;
                        if (nuevoEmail != null) email = nuevoEmail;

                        titulo2.setText(nombre.toUpperCase());
                        prefs.saveFull(nombre, email, prefs.getPassword(), prefs.isRecordar());
                    }
                }
        );

        // 🔹 Botón "Iniciar Compra"
        findViewById(R.id.btnIniciarCompra).setOnClickListener(v -> {
            Intent i = new Intent(this, ActividadSupermercados.class)
                    .putExtra("nombre", nombre)
                    .putExtra("email", email);
            startActivity(i);
        });

        // 🔹 Botón "Configurar Perfil"
        findViewById(R.id.btnConfigurarPerfil).setOnClickListener(v -> {
            Intent i = new Intent(this, ActividadPerfil.class)
                    .putExtra("nombre", nombre)
                    .putExtra("email", email);
            perfilLauncher.launch(i);
        });
    }

    // 🔹 Salir
    private boolean onToolbarItem(MenuItem item) {
        if (item.getItemId() == R.id.action_salir) {
            if (!prefs.isRecordar()) prefs.clearAll();

            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("fromLogout", true);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return true;
        }
        return false;
    }

    // 🔹 BottomSheet Perfil
    private void mostrarSheetPerfil() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.sheet_perfil, null);

        TextView tvNom = view.findViewById(R.id.txtNombrePerfil);
        TextView tvMail = view.findViewById(R.id.txtEmailPerfil);
        tvNom.setText(nombre.isEmpty() ? "Mi Perfil" : nombre);
        tvMail.setText(email.isEmpty() ? "usuario@correo.com" : email);

        // 🔹 Botón General
        view.findViewById(R.id.btnGeneral).setOnClickListener(v -> {
            sheet.dismiss();
            Intent i = new Intent(this, ActividadPerfil.class)
                    .putExtra("nombre", nombre)
                    .putExtra("email", email);
            perfilLauncher.launch(i);
        });

        // 🔹 Botón Alertas
        view.findViewById(R.id.btnAlertas).setOnClickListener(v -> {
            sheet.dismiss();
            startActivity(new Intent(this, ActividadSugerencias.class)
                    .putExtra("nombre", nombre)
                    .putExtra("email", email));
        });

        // 🔹 Botón Modo Niños
        view.findViewById(R.id.btnModoNinos).setOnClickListener(v -> {
            sheet.dismiss();
            Intent i = new Intent(this, ActividadModoNinos.class);
            startActivity(i);
        });

        // 🔹 Botón Control de Compras (solo visible si es Premium)
        View btnControlCompras = view.findViewById(R.id.btnControlCompras);
        if (btnControlCompras != null) {
            if (prefs.isPremium()) {
                btnControlCompras.setVisibility(View.VISIBLE);
                btnControlCompras.setOnClickListener(v -> {
                    sheet.dismiss();
                    Intent intent = new Intent(this, ActividadControlCompras.class);
                    intent.putExtra("nombre", nombre);
                    intent.putExtra("email", email);
                    startActivity(intent);
                });
            } else {
                btnControlCompras.setVisibility(View.GONE);
            }
        }

        // 🔹 Botón Premium visible solo si no es Premium
        View btnPremium = view.findViewById(R.id.btnPremiumPerfil);
        if (btnPremium != null) {
            if (prefs.isPremium()) {
                btnPremium.setVisibility(View.GONE);
            } else {
                btnPremium.setVisibility(View.VISIBLE);
                btnPremium.setOnClickListener(v -> {
                    sheet.dismiss();
                    startActivity(new Intent(this, com.example.carrishop.dialogos.ActividadPremium.class));
                });
            }
        }

        sheet.setContentView(view);
        sheet.show();
    }

}
