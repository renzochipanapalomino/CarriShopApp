package com.example.carrishop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.UsuarioEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.example.carrishop.datos.util.BackupManager;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText txtEmailLogin, txtContrasenaLogin;
    private CheckBox chkRecordar;
    private SesionPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtEmailLogin = findViewById(R.id.txtEmailLogin);
        txtContrasenaLogin = findViewById(R.id.txtContrasenaLogin);
        chkRecordar = findViewById(R.id.chkRecordar);
        prefs = new SesionPrefs(this);

        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);

        // 🔹 Restaurar respaldo si existe
        try {
            UsuarioEntidad respaldo = BackupManager.restaurar(this);
            if (respaldo != null) {
                UsuarioEntidad existente = db.usuarioDao().porEmail(respaldo.email);
                if (existente == null) db.usuarioDao().insertar(respaldo);

                prefs.saveFull(respaldo.nombre, respaldo.email, respaldo.contrasena, true);
                prefs.setPremium(respaldo.esPremium);
                prefs.setUsuarioId(respaldo.id);
                txtEmailLogin.setText(respaldo.email);
                txtContrasenaLogin.setText(respaldo.contrasena);
                chkRecordar.setChecked(true);
            }
        } catch (Exception e) {
            Log.e("BackupRestore", "Error al restaurar respaldo", e);
        }

        // 🔹 Autorellenar sesión guardada
        txtEmailLogin.setText(prefs.getEmail());
        txtContrasenaLogin.setText(prefs.getPassword());
        chkRecordar.setChecked(prefs.isRecordar());

        findViewById(R.id.btnIniciarSesion).setOnClickListener(v -> iniciarSesion());
        findViewById(R.id.btnIrRegistro).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadRegistro.class)));

        TextView linkOlvido = findViewById(R.id.txtOlvidaste);
        if (linkOlvido != null) {
            linkOlvido.setOnClickListener(v ->
                    startActivity(new Intent(this, ActividadOlvideContrasena.class)));
        }
    }

    private void iniciarSesion() {
        String email = safe(txtEmailLogin);
        String contrasena = safe(txtContrasenaLogin);

        if (email.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Escribe correo y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
        UsuarioEntidad u = db.usuarioDao().login(email, contrasena);

        if (u == null) {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean recordar = chkRecordar.isChecked();
        String nombreSeguro = (u.nombre != null && !u.nombre.trim().isEmpty()) ? u.nombre.trim() : "Usuario";

        // ✅ Guardar sesión
        prefs.saveFull(nombreSeguro, u.email, contrasena, recordar);
        prefs.setPremium(u.esPremium);
        prefs.setUsuarioId(u.id);
        if (!u.esPremium) prefs.resetPuntos();

        BackupManager.guardar(this, u);

        // ✅ Siempre dirigir a Bienvenida (flujo unificado)
        Intent i = new Intent(this, ActividadBienvenida.class);
        i.putExtra("nombre", nombreSeguro);
        i.putExtra("email", u.email);
        i.putExtra("esPremium", u.esPremium);
        startActivity(i);
        finish();
    }

    private String safe(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }
}
