package com.example.carrishop;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.UsuarioEntidad;
import com.example.carrishop.datos.util.BackupManager;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class ActividadRegistro extends AppCompatActivity {

    private TextInputEditText txtNombre, txtEmail, txtContrasena, txtTelefono, txtDni;
    private MaterialCheckBox chkTerminos, chkDatos, chkPrivacidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtEmail);
        txtContrasena = findViewById(R.id.txtContrasena);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtDni = findViewById(R.id.txtDni);
        chkTerminos = findViewById(R.id.chkTerminos);
        chkDatos = findViewById(R.id.chkDatos);
        chkPrivacidad = findViewById(R.id.chkPrivacidad);

        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardarUsuario());
    }

    private void guardarUsuario() {
        String nombre = get(txtNombre);
        String email = get(txtEmail);
        String contrasena = get(txtContrasena);
        String telefono = get(txtTelefono);
        String dni = get(txtDni);

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)
                || TextUtils.isEmpty(telefono) || TextUtils.isEmpty(dni)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.length() != 8) {
            Toast.makeText(this, "El DNI debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!esContrasenaSegura(contrasena)) {
            Toast.makeText(this, "La contraseña debe tener entre 8 y 25 caracteres, incluir mayúscula, minúscula, número y símbolo.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!chkTerminos.isChecked() || !chkDatos.isChecked() || !chkPrivacidad.isChecked()) {
            Toast.makeText(this, "Debes aceptar todos los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
        if (db.usuarioDao().porEmail(email) != null) {
            Toast.makeText(this, "Ese correo ya está registrado", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioEntidad u = new UsuarioEntidad();
        u.nombre = nombre;
        u.email = email;
        u.contrasena = contrasena;
        u.telefono = telefono;
        u.dni = dni;

        long id = db.usuarioDao().insertar(u);
        if (id > 0) {
            Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show();

            // 🔹 Crear respaldo JSON automáticamente
            BackupManager.guardar(this, u);

            finish();
        } else {
            Toast.makeText(this, "No se pudo crear la cuenta", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean esContrasenaSegura(String contrasena) {
        Pattern patron = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,25}$");
        return patron.matcher(contrasena).matches();
    }

    private String get(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }
}
