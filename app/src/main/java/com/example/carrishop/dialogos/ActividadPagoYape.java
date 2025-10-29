package com.example.carrishop.dialogos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrishop.R;
import com.example.carrishop.datos.prefs.SesionPrefs;

public class ActividadPagoYape extends AppCompatActivity {

    private TextView txtTotal;
    private EditText txtCodigoOperacion;
    private EditText txtCorreo;
    private Button btnConfirmar;
    private SesionPrefs sesionPrefs;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago_yape);

        sesionPrefs = new SesionPrefs(this);

        txtTotal = findViewById(R.id.txtTotalYape);
        txtCodigoOperacion = findViewById(R.id.txtCodigoOperacion);
        txtCorreo = findViewById(R.id.txtCorreoYape);
        btnConfirmar = findViewById(R.id.btnConfirmarYape);

        total = getIntent().getDoubleExtra("total", 9.90);
        txtTotal.setText("Total: PEN " + String.format("%.2f", total));

        txtCodigoOperacion.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        txtCodigoOperacion.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});

        btnConfirmar.setEnabled(false);
        btnConfirmar.setAlpha(0.5f);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarCampos();
            }
        };

        txtCodigoOperacion.addTextChangedListener(watcher);
        txtCorreo.addTextChangedListener(watcher);

        btnConfirmar.setOnClickListener(v -> procesarPago());
    }

    private void validarCampos() {
        boolean codigoValido = txtCodigoOperacion.getText().toString().trim().length() == 8;
        boolean correoValido = !txtCorreo.getText().toString().trim().isEmpty();

        boolean habilitar = codigoValido && correoValido;
        btnConfirmar.setEnabled(habilitar);
        btnConfirmar.setAlpha(habilitar ? 1f : 0.5f);
    }

    private void procesarPago() {
        String codigo = txtCodigoOperacion.getText().toString().trim();
        String correo = txtCorreo.getText().toString().trim();

        if (codigo.length() != 8) {
            Toast.makeText(this, "Verifica el código de operación ingresado.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correo.isEmpty()) {
            Toast.makeText(this, "Ingresa el correo asociado a tu cuenta.", Toast.LENGTH_SHORT).show();
            return;
        }

        sesionPrefs.setPremium(true);

        Toast.makeText(this, "Pago con Yape verificado. ¡Gracias!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, ActividadConfirmacionPago.class);
        intent.putExtra("total", total);
        intent.putExtra("correo_usuario", correo);
        intent.putExtra("metodo_pago", "Yape");
        startActivity(intent);
        finish();
    }
}
