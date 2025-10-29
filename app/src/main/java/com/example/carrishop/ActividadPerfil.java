package com.example.carrishop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.UsuarioEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ActividadPerfil extends AppCompatActivity {

    private TextInputEditText txtEmail, txtNombre, txtTelefono;
    private MaterialButton btnEditar, btnGuardar, btnSalir;
    private String emailArg = "";
    private boolean editMode = false;
    private SesionPrefs sesionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicialización de vistas
        txtEmail    = findViewById(R.id.txtEmailPerfil);
        txtNombre   = findViewById(R.id.txtNombrePerfil);
        txtTelefono = findViewById(R.id.txtTelefonoPerfil);
        btnEditar   = findViewById(R.id.btnEditarPerfil);
        btnGuardar  = findViewById(R.id.btnGuardarPerfil);
        btnSalir    = findViewById(R.id.btnSalirPerfil);

        sesionPrefs = new SesionPrefs(this);

        // Email recibido desde bienvenida o login
        emailArg = getIntent().getStringExtra("email");
        if (emailArg == null) emailArg = sesionPrefs.getEmail();

        cargarPerfil();
        setEditable(false);

        // Botón editar
        btnEditar.setOnClickListener(v -> {
            editMode = true;
            setEditable(true);
            btnGuardar.setVisibility(android.view.View.VISIBLE);
        });

        // Botón guardar cambios
        btnGuardar.setOnClickListener(v -> guardarCambios(true));

        // Botón salir
        btnSalir.setOnClickListener(v -> {
            if (editMode) {
                guardarCambios(true);
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void cargarPerfil() {
        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
        UsuarioEntidad u = null;

        if (!TextUtils.isEmpty(emailArg)) {
            u = db.usuarioDao().porEmail(emailArg);
        }

        if (u != null) {
            txtEmail.setText(u.email);
            txtNombre.setText(empty(u.nombre) ? "" : u.nombre);
            txtTelefono.setText(empty(u.telefono) ? "" : u.telefono);
        } else {
            txtEmail.setText(emailArg);
            txtNombre.setText(empty(sesionPrefs.getNombre()) ? "" : sesionPrefs.getNombre());
            txtTelefono.setText("");
        }
    }

    private void setEditable(boolean enabled) {
        txtEmail.setEnabled(false);
        txtEmail.setFocusable(false);

        txtNombre.setEnabled(enabled);
        txtNombre.setFocusableInTouchMode(enabled);

        txtTelefono.setEnabled(enabled);
        txtTelefono.setFocusableInTouchMode(enabled);
    }

    private void guardarCambios(boolean cerrarLuego) {
        String email = txtEmail.getText() == null ? "" : txtEmail.getText().toString().trim();
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().toString().trim();
        String telefono = txtTelefono.getText() == null ? "" : txtTelefono.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "No hay un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
        int filas = db.usuarioDao().actualizarPerfil(email, nombre, telefono);

        if (filas > 0) {
            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();

            sesionPrefs.saveFull(nombre, email, sesionPrefs.getPassword(), sesionPrefs.isRecordar());

            Intent data = new Intent();
            data.putExtra("email", email);
            data.putExtra("nombre", nombre);
            data.putExtra("telefono", telefono);
            setResult(RESULT_OK, data);

            if (cerrarLuego) finish();

            editMode = false;
            setEditable(false);
            btnGuardar.setVisibility(android.view.View.GONE);
        } else {
            Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
