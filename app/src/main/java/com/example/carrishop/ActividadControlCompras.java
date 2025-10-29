package com.example.carrishop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carrishop.datos.bd.BaseDeDatosApp;
import com.example.carrishop.datos.entidades.CompraEntidad;
import com.example.carrishop.datos.prefs.SesionPrefs;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 💎 Control de Compras Premium
 * Muestra gráficos de gasto semanal y mensual + historial.
 */
public class ActividadControlCompras extends AppCompatActivity {

    private SesionPrefs prefs;
    private BaseDeDatosApp db;
    private BarChart chartCompras;
    private RecyclerView recyclerHistorial;
    private Spinner spnModoGrafico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_compras);

        prefs = new SesionPrefs(this);
        db = BaseDeDatosApp.obtener(this);

        // 🚫 Solo Premium
        if (!prefs.isPremium()) {
            Toast.makeText(this, "Acceso exclusivo para usuarios Premium 💎", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        chartCompras = findViewById(R.id.chartCompras);
        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        spnModoGrafico = findViewById(R.id.spnModoGrafico);

        int idUsuario = prefs.obtenerUsuarioId();
        List<CompraEntidad> compras = db.compraDao().listarPorUsuario(idUsuario);

        if (compras.isEmpty()) {
            Toast.makeText(this, "Aún no tienes compras registradas.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Configurar selector de modo
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Semanal", "Mensual"}
        );
        spnModoGrafico.setAdapter(adapter);

        spnModoGrafico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mostrarGraficoSemanal(compras);
                } else {
                    mostrarGraficoMensual(compras);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mostrarHistorial(compras);
    }

    // =========================================================
    // 📊 GRAFICO SEMANAL
    // =========================================================
    private void mostrarGraficoSemanal(List<CompraEntidad> compras) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        double[] totalesPorDia = new double[7];
        long ahora = System.currentTimeMillis();
        long unDia = 24 * 60 * 60 * 1000L;

        for (CompraEntidad c : compras) {
            try {
                Date fecha = sdf.parse(c.fechaHora);
                if (fecha == null) continue;
                long diff = ahora - fecha.getTime();
                int diasAtras = (int) (diff / unDia);
                if (diasAtras < 7 && diasAtras >= 0) {
                    totalesPorDia[6 - diasAtras] += c.total;
                }
            } catch (ParseException ignored) {}
        }

        String[] dias = {"D-6", "D-5", "D-4", "D-3", "D-2", "D-1", "Hoy"};
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, (float) totalesPorDia[i]));
            labels.add(dias[i]);
        }

        dibujarGrafico(entries, labels, "Gasto últimos 7 días");
    }

    // =========================================================
    // 📅 GRAFICO MENSUAL (últimas 4 semanas)
    // =========================================================
    private void mostrarGraficoMensual(List<CompraEntidad> compras) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        double[] totalesPorSemana = new double[4];
        long ahora = System.currentTimeMillis();
        long unaSemana = 7L * 24 * 60 * 60 * 1000L;

        for (CompraEntidad c : compras) {
            try {
                Date fecha = sdf.parse(c.fechaHora);
                if (fecha == null) continue;
                long diff = ahora - fecha.getTime();
                int semanasAtras = (int) (diff / unaSemana);
                if (semanasAtras < 4 && semanasAtras >= 0) {
                    totalesPorSemana[3 - semanasAtras] += c.total;
                }
            } catch (ParseException ignored) {}
        }

        String[] semanas = {"Semana 1", "Semana 2", "Semana 3", "Semana 4"};
        for (int i = 0; i < 4; i++) {
            entries.add(new BarEntry(i, (float) totalesPorSemana[i]));
            labels.add(semanas[i]);
        }

        dibujarGrafico(entries, labels, "Resumen mensual (últimas 4 semanas)");
    }

    // =========================================================
    // 🎨 DIBUJAR GRAFICO
    // =========================================================
    private void dibujarGrafico(ArrayList<BarEntry> entries, ArrayList<String> labels, String titulo) {
        BarDataSet dataSet = new BarDataSet(entries, titulo);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        chartCompras.setData(data);
        chartCompras.setFitBars(true);
        chartCompras.animateY(1200);

        chartCompras.getXAxis().setGranularity(1f);
        chartCompras.getXAxis().setDrawGridLines(false);
        chartCompras.getAxisLeft().setDrawGridLines(false);
        chartCompras.getAxisRight().setEnabled(false);

        chartCompras.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) return labels.get(index);
                return "";
            }
        });

        Description desc = new Description();
        desc.setText(titulo);
        chartCompras.setDescription(desc);
        chartCompras.invalidate();
    }

    // =========================================================
    // 🧾 HISTORIAL DE COMPRAS
    // =========================================================
    private void mostrarHistorial(List<CompraEntidad> compras) {
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistorial.setAdapter(new HistorialCompraAdapter(compras));
    }
}
