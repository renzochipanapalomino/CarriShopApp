package com.example.carrishop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.adaptadores.ProductoAdaptador;
import com.example.carrishop.datos.modelos.Producto;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla moderna tipo catálogo de promociones y supermercados.
 * Muestra productos aleatorios al presionar los logos.
 */
public class ActividadSugerencias extends AppCompatActivity {

    private final List<Producto> catalogo = new ArrayList<>();
    private ProductoAdaptador adaptador;
    private EditText txtBuscar;

    private String supermercadoActual = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);

        // === RecyclerView ===
        RecyclerView rv = findViewById(R.id.recyclerSugerencias);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new ProductoAdaptador();
        rv.setAdapter(adaptador);

        // === Buscar ===
        txtBuscar = findViewById(R.id.txtBuscar);
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrar(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // === Cargar catálogo inicial ===
        cargarCatalogo();
        refrescar();

        // === Logos ===
        configurarLogos();

        // === Botón salir ===
        MaterialButton salir = findViewById(R.id.btnSalirSug);
        salir.setOnClickListener(v -> finish());
    }

    /** Configura los logos: cada click muestra productos aleatorios */
    private void configurarLogos() {
        int[] ids = {
                R.id.logoTottus, R.id.logoPlazaVea, R.id.logoMetro, R.id.logoMass, R.id.logoTambo
        };
        String[] nombres = {"Tottus", "PlazaVea", "Ripley", "Mass", "SagaFalabella"};

        for (int i = 0; i < ids.length; i++) {
            ImageView img = findViewById(ids[i]);
            String nombre = nombres[i];
            img.setOnClickListener(v -> {
                supermercadoActual = nombre;
                mostrarProductosAleatorios(nombre);
            });
        }

        HorizontalScrollView scroll = findViewById(R.id.scrollLogos);
        scroll.postDelayed(() -> scroll.fullScroll(HorizontalScrollView.FOCUS_LEFT), 100);
    }

    /** Carga el catálogo base */
    private void cargarCatalogo() {
        catalogo.clear();
        catalogo.add(new Producto(1, "Leche Gloria 1L", 12.90, R.drawable.prod_leche_gloria, true));
        catalogo.add(new Producto(2, "Arroz extra 5kg", 28.30, R.drawable.prod_arroz, true));
        catalogo.add(new Producto(3, "Huevos pardos", 9.90, R.drawable.prod_huevos, true));
        catalogo.add(new Producto(4, "Pan molde integral", 7.50, R.drawable.prod_pan, false));
        catalogo.add(new Producto(5, "Aceite vegetal 1L", 14.90, R.drawable.prod_aceite, false));
        catalogo.add(new Producto(6, "Atún en agua", 5.50, R.drawable.prod_atun, true));
        catalogo.add(new Producto(7, "Café instantáneo", 15.90, R.drawable.prod_cafe, true));
        catalogo.add(new Producto(8, "Azúcar rubia 1kg", 4.90, R.drawable.prod_azucar, false));
        catalogo.add(new Producto(9, "Gaseosa 1.5L", 6.70, R.drawable.prod_gaseosa, false));
        catalogo.add(new Producto(10, "Fideos spaghetti 500g", 4.30, R.drawable.prod_fideos, false));
    }

    /** Muestra una lista aleatoria de productos cuando se toca un logo */
    private void mostrarProductosAleatorios(String supermercado) {
        List<Producto> copia = new ArrayList<>(catalogo);
        Collections.shuffle(copia); // Mezcla los productos aleatoriamente

        // Muestra solo 4 a 6 productos diferentes cada vez
        int cantidad = 4 + (int)(Math.random() * 3);
        List<Producto> seleccion = new ArrayList<>(copia.subList(0, Math.min(cantidad, copia.size())));

        adaptador.submit(seleccion);
    }

    /** Refresca la lista completa (por defecto “Todos”) */
    private void refrescar() {
        adaptador.submit(new ArrayList<>(catalogo));
    }

    /** Filtra por texto (buscador) */
    private void filtrar(String texto) {
        if (texto.isEmpty()) {
            refrescar();
            return;
        }

        List<Producto> filtrados = new ArrayList<>();
        for (Producto p : catalogo) {
            if (p.nombre.toLowerCase().contains(texto.toLowerCase())) {
                filtrados.add(p);
            }
        }
        adaptador.submit(filtrados);
    }
}
