package com.example.carrishop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.adaptadores.ListaMercadoAdapter;
import com.example.carrishop.datos.modelos.ListaMercadoItem;
import com.example.carrishop.datos.modelos.ProductoConPrecio;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.example.carrishop.datos.sqlite.DbHelper;
import com.example.carrishop.datos.sqlite.ListaMercadoDao;
import com.example.carrishop.datos.sqlite.ProductoPrecioDao;
import com.example.carrishop.dialogos.DialogoSeleccionProducto;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ActividadListaMercado extends AppCompatActivity implements ListaMercadoAdapter.Callbacks {

    private static final int[] SUPER_IDS = {1, 2, 3, 4, 5};
    private static final String[] SUPER_NOMBRES = {
            "Tottus", "Plaza Vea", "Saga Falabella", "Ripley", "Mass"
    };

    private AutoCompleteTextView autoSuper;
    private TextInputEditText txtProducto;
    private TextView txtEmpty;
    private ListaMercadoAdapter adapter;

    private ListaMercadoDao listaDao;
    private ProductoPrecioDao precioDao;
    private SharedPreferences configPrefs;
    private SesionPrefs sesionPrefs;

    private int superSeleccionado = -1;
    private String nombreSuperSeleccionado = "";
    private String nombreUsuario = "";
    private String emailUsuario = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mercado);

        MaterialToolbar toolbar = findViewById(R.id.toolbarLista);
        toolbar.setNavigationOnClickListener(v -> finish());

        autoSuper = findViewById(R.id.autoSupermercados);
        txtProducto = findViewById(R.id.txtProducto);
        txtEmpty = findViewById(R.id.txtEmpty);
        RecyclerView recycler = findViewById(R.id.recyclerLista);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListaMercadoAdapter(this);
        recycler.setAdapter(adapter);

        DbHelper helper = new DbHelper(this);
        listaDao = new ListaMercadoDao(helper);
        precioDao = new ProductoPrecioDao(helper);
        configPrefs = getSharedPreferences("lista_mercado", MODE_PRIVATE);
        sesionPrefs = new SesionPrefs(this);

        nombreUsuario = valorSeguro("nombre");
        emailUsuario = valorSeguro("email");

        // Configurar super seleccionado
        int superExtra = getIntent().getIntExtra("superId", -1);
        if (superExtra > 0) {
            superSeleccionado = superExtra;
            nombreSuperSeleccionado = getIntent().getStringExtra("superNombre");
            if (nombreSuperSeleccionado == null || nombreSuperSeleccionado.isEmpty()) {
                nombreSuperSeleccionado = obtenerNombreSuper(superSeleccionado);
            }
            guardarPreferenciaSuper();
        } else {
            superSeleccionado = configPrefs.getInt("super_id", -1);
            nombreSuperSeleccionado = configPrefs.getString("super_nombre", "");
        }

        if (nombreUsuario.isEmpty()) nombreUsuario = sesionPrefs.getNombre();
        if (emailUsuario.isEmpty()) emailUsuario = sesionPrefs.getEmail();

        ArrayAdapter<String> supAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, SUPER_NOMBRES);
        autoSuper.setAdapter(supAdapter);
        autoSuper.setOnItemClickListener((parent, view, position, id) -> {
            superSeleccionado = SUPER_IDS[position];
            nombreSuperSeleccionado = SUPER_NOMBRES[position];
            guardarPreferenciaSuper();
        });
        autoSuper.setOnClickListener(v -> autoSuper.showDropDown());
        actualizarTextoSuper();

        MaterialButton btnAgregar = findViewById(R.id.btnAgregarProducto);
        btnAgregar.setOnClickListener(v -> agregarProductoManual());

        MaterialButton btnGuardar = findViewById(R.id.btnGuardarLista);
        btnGuardar.setOnClickListener(v -> Toast.makeText(this,
                R.string.lista_guardada, Toast.LENGTH_SHORT).show());

        MaterialButton btnIrVoz = findViewById(R.id.btnIrVoz);
        btnIrVoz.setOnClickListener(v -> irASesionVoz());

        cargarDatos();
    }

    private void agregarProductoManual() {
        if (superSeleccionado <= 0) {
            Toast.makeText(this, R.string.lista_error_super, Toast.LENGTH_SHORT).show();
            return;
        }
        String nombre = obtenerTexto(txtProducto);
        if (nombre.isEmpty()) {
            Toast.makeText(this, R.string.lista_error_nombre, Toast.LENGTH_SHORT).show();
            return;
        }
        long nuevoId = listaDao.insertarManual(nombre, superSeleccionado);
        if (nuevoId == -1) {
            Toast.makeText(this, R.string.lista_error_bd, Toast.LENGTH_SHORT).show();
            return;
        }
        txtProducto.setText("");
        cargarDatos();
    }

    private void cargarDatos() {
        List<ListaMercadoItem> data = listaDao.obtenerTodos();
        adapter.actualizar(data);
        txtEmpty.setVisibility(data == null || data.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void irASesionVoz() {
        if (superSeleccionado <= 0) {
            Toast.makeText(this, R.string.lista_error_super, Toast.LENGTH_SHORT).show();
            return;
        }
        if (nombreSuperSeleccionado == null || nombreSuperSeleccionado.isEmpty()) {
            nombreSuperSeleccionado = obtenerNombreSuper(superSeleccionado);
        }
        Intent i = new Intent(this, ActividadEscanearVoz.class);
        i.putExtra("superId", superSeleccionado);
        i.putExtra("superNombre", nombreSuperSeleccionado);
        i.putExtra("nombre", nombreUsuario);
        i.putExtra("email", emailUsuario);
        startActivity(i);
    }

    private void actualizarTextoSuper() {
        if (superSeleccionado <= 0) return;
        for (int i = 0; i < SUPER_IDS.length; i++) {
            if (SUPER_IDS[i] == superSeleccionado) {
                autoSuper.setText(SUPER_NOMBRES[i], false);
                return;
            }
        }
        if (nombreSuperSeleccionado != null && !nombreSuperSeleccionado.isEmpty()) {
            autoSuper.setText(nombreSuperSeleccionado, false);
        }
    }

    private String obtenerNombreSuper(int id) {
        for (int i = 0; i < SUPER_IDS.length; i++) {
            if (SUPER_IDS[i] == id) return SUPER_NOMBRES[i];
        }
        return "";
    }

    private void guardarPreferenciaSuper() {
        configPrefs.edit()
                .putInt("super_id", superSeleccionado)
                .putString("super_nombre", nombreSuperSeleccionado)
                .apply();
    }

    private String obtenerTexto(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String valorSeguro(String extra) {
        String valor = getIntent().getStringExtra(extra);
        return valor == null ? "" : valor;
    }

    @Override
    public void onMarcar(ListaMercadoItem item) {
        if (superSeleccionado <= 0) {
            Toast.makeText(this, R.string.lista_error_super, Toast.LENGTH_SHORT).show();
            cargarDatos();
            return;
        }
        List<ProductoConPrecio> variantes = precioDao.buscarVariantesPorNombre(item.nombre, superSeleccionado);
        if (variantes == null || variantes.isEmpty()) {
            Toast.makeText(this, R.string.lista_no_disponible, Toast.LENGTH_SHORT).show();
            cargarDatos();
            return;
        }
        DialogoSeleccionProducto dialogo = new DialogoSeleccionProducto(variantes, seleccionado -> {
            listaDao.actualizarConPrecio(item.id, seleccionado.precio, seleccionado.cantidad,
                    false, superSeleccionado);
            Toast.makeText(this, R.string.lista_guardada, Toast.LENGTH_SHORT).show();
            cargarDatos();
        });
        dialogo.show(getSupportFragmentManager(), "DialogoListaMercado");
    }

    @Override
    public void onEliminar(ListaMercadoItem item) {
        listaDao.eliminar(item.id);
        cargarDatos();
    }
}
