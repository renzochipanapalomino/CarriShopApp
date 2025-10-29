package com.example.carrishop;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.google.android.material.textfield.TextInputEditText;

public class ActividadOlvideContrasena extends AppCompatActivity {

    private TextInputEditText txtEmailRecuperar, txtDniRecuperar, txtNuevaContrasena, txtRepetirContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvide_contrasena);

        // Enlazar los campos con los IDs actuales del XML
        txtEmailRecuperar = findViewById(R.id.txtEmailRecuperar);
        txtDniRecuperar = findViewById(R.id.txtDniRecuperar);
        txtNuevaContrasena = findViewById(R.id.txtNuevaContrasena);
        txtRepetirContrasena = findViewById(R.id.txtRepetirContrasena);

        findViewById(R.id.btnActualizarContrasena).setOnClickListener(v -> actualizar());
    }

    private void actualizar() {
        String email = val(txtEmailRecuperar);
        String dni = val(txtDniRecuperar);
        String p1 = val(txtNuevaContrasena);
        String p2 = val(txtRepetirContrasena);

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(dni) ||
                TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dni.length() != 8) {
            Toast.makeText(this, "El DNI debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!p1.equals(p2)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        BaseDeDatosApp db = BaseDeDatosApp.obtener(this);
        if (db.usuarioDao().porEmail(email) == null) {
            Toast.makeText(this, "Ese correo no existe", Toast.LENGTH_SHORT).show();
            return;
        }

        int rows = db.usuarioDao().actualizarContrasena(email, p1);
        if (rows > 0) {
            Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "No se pudo actualizar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private String val(TextInputEditText t) {
        return t.getText() == null ? "" : t.getText().toString().trim();
    }
}
