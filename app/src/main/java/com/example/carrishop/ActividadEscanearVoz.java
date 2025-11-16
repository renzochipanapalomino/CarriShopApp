package com.example.carrishop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.adaptadores.ProductoCapturadoAdaptador;
import com.example.carrishop.datos.modelos.ListaMercadoItem;
import com.example.carrishop.datos.modelos.ProductoCapturado;
import com.example.carrishop.datos.modelos.ProductoConPrecio;
import com.example.carrishop.datos.sqlite.DbHelper;
import com.example.carrishop.datos.sqlite.ListaMercadoDao;
import com.example.carrishop.datos.sqlite.ProductoPrecioDao;
import com.example.carrishop.dialogos.DialogoSeleccionProducto;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActividadEscanearVoz extends AppCompatActivity {

    private final ArrayList<ProductoCapturado> capturados = new ArrayList<>();
    private ProductoCapturadoAdaptador adaptador;
    private DbHelper dbHelper;
    private ProductoPrecioDao precioDao;
    private ListaMercadoDao listaDao;

    private int superId = -1;
    private String superNombre = "";
    private String nombreUsuario = "";
    private String emailUsuario = "";

    private ActivityResultLauncher<Intent> lanzadorVoz;
    private ActivityResultLauncher<String> pedirPermisoAudio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escanear_voz);

        // --- Extras recibidos ---
        superId = getIntent().getIntExtra("superId", -1);
        superNombre = safeStr(getIntent().getStringExtra("superNombre"));
        nombreUsuario = safeStr(getIntent().getStringExtra("nombre"));
        emailUsuario = safeStr(getIntent().getStringExtra("email"));

        // --- Mostrar nombre del supermercado ---
        TextView txtSuper = findViewById(R.id.txtSupermercado);
        txtSuper.setText(superNombre.isEmpty() ? "Supermercado" : superNombre);

        // --- BD local ---
        dbHelper = new DbHelper(this);
        precioDao = new ProductoPrecioDao(dbHelper);
        listaDao = new ListaMercadoDao(dbHelper);

        // --- Recycler ---
        RecyclerView rv = findViewById(R.id.recyclerVoz);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new ProductoCapturadoAdaptador(capturados, position -> {
            if (position >= 0 && position < capturados.size()) {
                ProductoCapturado eliminado = capturados.remove(position);
                if (eliminado.listaId > 0) listaDao.eliminar(eliminado.listaId);
                adaptador.notifyItemRemoved(position);
                adaptador.notifyItemRangeChanged(position, capturados.size() - position);
            }
        });
        rv.setAdapter(adaptador);

        // --- Cargar lista persistente ---
        cargarDesdeListaMercado();

        // --- Lanzador de voz ---
        lanzadorVoz = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> frases = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (frases != null && !frases.isEmpty()) {
                            manejarFrase(frases.get(0));
                        } else {
                            Toast.makeText(this, "No se reconoció ninguna frase", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // --- Permiso de micrófono ---
        pedirPermisoAudio = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) iniciarDictado();
                    else Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
                });

        ImageView btnMic = findViewById(R.id.btnMic);
        btnMic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                iniciarDictado();
            } else {
                pedirPermisoAudio.launch(Manifest.permission.RECORD_AUDIO);
            }
        });

        findViewById(R.id.btnIrListaMercado).setOnClickListener(v -> {
            Intent intent = new Intent(this, ActividadListaMercado.class);
            intent.putExtra("superId", superId);
            intent.putExtra("superNombre", superNombre);
            intent.putExtra("nombre", nombreUsuario);
            intent.putExtra("email", emailUsuario);
            startActivity(intent);
        });

        // --- Botón para ir al resumen ---
        MaterialButton btnCarrito = findViewById(R.id.btnAgregarCarrito);
        btnCarrito.setOnClickListener(v -> {
            if (capturados.isEmpty()) {
                Toast.makeText(this, "Tu lista está vacía", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ActividadResumenCompra.class);
            i.putExtra("items", new ArrayList<>(capturados));
            i.putExtra("nombre", nombreUsuario);
            i.putExtra("email", emailUsuario);
            i.putExtra("superNombre", superNombre);
            startActivity(i);
        });
    }

    // ========== VOZ ==========
    private void iniciarDictado() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("es", "PE"));
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di el nombre del producto");
        lanzadorVoz.launch(i);
    }

    private void manejarFrase(String fraseReconocida) {
        if (fraseReconocida == null || fraseReconocida.trim().isEmpty()) return;

        List<ProductoConPrecio> variantes = precioDao.buscarVariantesPorNombre(fraseReconocida, superId);

        if (variantes == null || variantes.isEmpty()) {
            Toast.makeText(this, "No se encontró el producto \"" + fraseReconocida + "\"", Toast.LENGTH_SHORT).show();
            return;
        }

        if (variantes.size() == 1) {
            ProductoConPrecio p = variantes.get(0);
            agregarALista(p.nombre, p.precio, 1);
        } else {
            DialogoSeleccionProducto dialogo = new DialogoSeleccionProducto(
                    variantes,
                    productoSeleccionado -> agregarALista(
                            productoSeleccionado.nombre,
                            productoSeleccionado.precio,
                            productoSeleccionado.cantidad
                    )
            );
            dialogo.show(getSupportFragmentManager(), "DialogoSeleccionProducto");
        }
    }

    // ========== CARRITO ==========
    private void agregarALista(String nombre, double precioUnitario, int cantidad) {
        ProductoCapturado nuevo = new ProductoCapturado(nombre, precioUnitario, cantidad);
        long id = listaDao.insertarDesdeVoz(nombre, precioUnitario, cantidad, superId);
        nuevo.listaId = id;
        capturados.add(nuevo);
        adaptador.notifyItemInserted(capturados.size() - 1);
    }

    private void cargarDesdeListaMercado() {
        capturados.clear();
        List<ListaMercadoItem> data = listaDao.obtenerConPrecio(superId);
        if (data != null) {
            for (ListaMercadoItem item : data) {
                ProductoCapturado p = new ProductoCapturado(item.nombre, item.precio, item.cantidad, item.id);
                capturados.add(p);
            }
        }
        adaptador.notifyDataSetChanged();
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }

    /** 🧹 Eliminar carrito guardado al finalizar compra */
    public static void limpiarCarrito(Context ctx) {
        DbHelper helper = new DbHelper(ctx);
        new ListaMercadoDao(helper).limpiarSincronizados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDesdeListaMercado();
    }
}
