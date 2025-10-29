package com.example.carrishop.dialogos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.R;
import com.example.carrishop.datos.prefs.SesionPrefs;

public class ActividadPagoTarjeta extends AppCompatActivity {

    private EditText txtNumero, txtFecha, txtCvv, txtTitular, txtCorreo;
    private Button btnConfirmar;
    private SesionPrefs sesionPrefs;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_tarjeta);

        sesionPrefs = new SesionPrefs(this);

        txtNumero = findViewById(R.id.txtNumeroTarjeta);
        txtFecha = findViewById(R.id.txtFechaTarjeta);
        txtCvv = findViewById(R.id.txtCvvTarjeta);
        txtTitular = findViewById(R.id.txtTitularTarjeta);
        txtCorreo = findViewById(R.id.txtCorreoTarjeta);
        btnConfirmar = findViewById(R.id.btnConfirmarPago);

        // 🔹 Total recibido (por si se envía desde checkout)
        total = getIntent().getDoubleExtra("total", 9.90);

        // 🔹 Configurar restricciones de entrada
        txtNumero.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        txtNumero.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        txtFecha.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        txtFecha.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)}); // MMYY
        txtCvv.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        txtCvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        // 🔹 Deshabilitar botón al inicio
        btnConfirmar.setEnabled(false);
        btnConfirmar.setAlpha(0.5f);

        // 🔹 Validación en tiempo real
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarCampos();
            }
        };
        txtNumero.addTextChangedListener(watcher);
        txtFecha.addTextChangedListener(watcher);
        txtCvv.addTextChangedListener(watcher);
        txtTitular.addTextChangedListener(watcher);
        txtCorreo.addTextChangedListener(watcher);

        // 🔹 Acción del botón confirmar
        btnConfirmar.setOnClickListener(v -> procesarPago());
    }

    /** 🔸 Verifica si los campos están completos para habilitar el botón */
    private void validarCampos() {
        boolean numeroValido = txtNumero.getText().toString().trim().length() == 16;
        boolean fechaValida = txtFecha.getText().toString().trim().length() == 4;
        boolean cvvValido = txtCvv.getText().toString().trim().length() == 3;
        boolean titularValido = !txtTitular.getText().toString().trim().isEmpty();
        boolean correoValido = !txtCorreo.getText().toString().trim().isEmpty();

        boolean habilitar = numeroValido && fechaValida && cvvValido && titularValido && correoValido;

        btnConfirmar.setEnabled(habilitar);
        btnConfirmar.setAlpha(habilitar ? 1f : 0.5f);
    }

    /** 🔸 Simula el pago exitoso y pasa datos a la pantalla de confirmación */
    private void procesarPago() {
        String correoIngresado = txtCorreo.getText().toString().trim();

        if (correoIngresado.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo asociado a la cuenta.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Guardar temporalmente el estado Premium local (para coherencia inmediata)
        sesionPrefs.setPremium(true);

        Toast.makeText(this, "Pago exitoso. Procesando confirmación...", Toast.LENGTH_LONG).show();

        // 🔹 Ir a la pantalla de confirmación y pasar correo + total
        Intent intent = new Intent(this, ActividadConfirmacionPago.class);
        intent.putExtra("total", total);
        intent.putExtra("correo_usuario", correoIngresado); // 🔸 NUEVO
        intent.putExtra("metodo_pago", "Tarjeta");
        startActivity(intent);
        finish();
    }
}
