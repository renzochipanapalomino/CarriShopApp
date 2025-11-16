package com.example.carrishop.datos.sqlite;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.carrishop.datos.modelos.ProductoConPrecio;
import com.example.carrishop.datos.util.TextoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO de lectura directa (SQLite) para consultar productos con precios.
 * Permite búsquedas exactas, flexibles y por variantes de marca.
 */
public class ProductoPrecioDao {

    private final DbHelper helper;
    private static final String TAG = "ProductoPrecioDao";

    public ProductoPrecioDao(DbHelper helper) {
        this.helper = helper;
    }

    // ===========================================================
    // 🔍 MÉTODO DE VERIFICACIÓN DE SEMILLA
    // ===========================================================
    /** Muestra un resumen simple de productos y precios por supermercado. */
    public String debugResumen(long superId) {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            long productos = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM productos", null);
            long precios = DatabaseUtils.longForQuery(db,
                    "SELECT COUNT(*) FROM precios WHERE supermercado_id=?",
                    new String[]{ String.valueOf(superId) });
            return "Productos=" + productos + ", Precios(super " + superId + ")=" + precios;
        } catch (SQLiteException e) {
            Log.e(TAG, "Error obteniendo resumen", e);
            return "Productos=0, Precios(super " + superId + ")=0";
        }
    }

    // ===========================================================
    // 🔹 BÚSQUEDA RÁPIDA: DEVUELVE UN SOLO PRODUCTO COINCIDENTE
    // ===========================================================
    @Nullable
    public ProductoConPrecio buscarPorNombreEnSuper(String nombreDicho, long superId) {
        if (superId <= 0) return null;

        final String q = TextoUtils.normalizar(nombreDicho);
        try {
            final SQLiteDatabase db = helper.getReadableDatabase();

            String sqlExacto =
                    "SELECT p.id, p.nombre, p.marca, p.categoria, pr.precio " +
                            "FROM productos p " +
                            "JOIN precios pr ON pr.producto_id = p.id " +
                            "WHERE pr.supermercado_id = ? " +
                            "AND p.nombre_normalizado = ? " +
                            "LIMIT 1";

            ProductoConPrecio exacto = leerUno(db.rawQuery(sqlExacto, new String[]{
                    String.valueOf(superId), q
            }));
            if (exacto != null) return exacto;

            String sqlFlexible =
                    "SELECT p.id, p.nombre, p.marca, p.categoria, pr.precio " +
                            "FROM productos p " +
                            "JOIN precios pr ON pr.producto_id = p.id " +
                            "WHERE pr.supermercado_id = ? " +
                            "AND (INSTR(p.nombre_normalizado, ?) > 0 OR INSTR(?, p.nombre_normalizado) > 0) " +
                            "ORDER BY LENGTH(p.nombre_normalizado) ASC " +
                            "LIMIT 1";

            return leerUno(db.rawQuery(sqlFlexible, new String[]{
                    String.valueOf(superId), q, q
            }));
        } catch (SQLiteException e) {
            Log.e(TAG, "Error buscando producto por nombre", e);
            return null;
        }
    }

    // ===========================================================
    // 🆕 NUEVA FUNCIÓN: DEVUELVE TODAS LAS VARIANTES COINCIDENTES
    // ===========================================================
    /**
     * Devuelve una lista de productos (variantes por marca) que coinciden
     * parcialmente con el nombre dicho, dentro de un supermercado.
     */
    public List<ProductoConPrecio> buscarVariantesPorNombre(String nombreDicho, long superId) {
        List<ProductoConPrecio> lista = new ArrayList<>();
        if (superId <= 0 || nombreDicho == null || nombreDicho.trim().isEmpty()) return lista;

        final String q = TextoUtils.normalizar(nombreDicho);
        Cursor c = null;
        try {
            final SQLiteDatabase db = helper.getReadableDatabase();
            String sql =
                    "SELECT p.id, p.nombre, p.marca, p.categoria, pr.precio " +
                            "FROM productos p " +
                            "JOIN precios pr ON pr.producto_id = p.id " +
                            "WHERE pr.supermercado_id = ? " +
                            "AND (INSTR(p.nombre_normalizado, ?) > 0 OR INSTR(?, p.nombre_normalizado) > 0) " +
                            "ORDER BY p.marca ASC, LENGTH(p.nombre_normalizado) ASC " +
                            "LIMIT 30";

            c = db.rawQuery(sql, new String[]{ String.valueOf(superId), q, q });
            while (c.moveToNext()) {
                ProductoConPrecio p = new ProductoConPrecio();
                p.productoId = c.getLong(0);
                p.nombre     = c.getString(1);
                p.marca      = c.getString(2);
                p.categoria  = c.getString(3);
                p.precio     = c.getDouble(4);
                lista.add(p);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error buscando variantes", e);
        } finally {
            if (c != null) c.close();
        }

        return lista;
    }

    // ===========================================================
    // 🔸 FUNCIÓN INTERNA: LECTURA DE UN SOLO REGISTRO
    // ===========================================================
    private @Nullable ProductoConPrecio leerUno(Cursor c) {
        try {
            if (c.moveToFirst()) {
                ProductoConPrecio r = new ProductoConPrecio();
                r.productoId = c.getLong(0);
                r.nombre     = c.getString(1);
                r.marca      = c.getString(2);
                r.categoria  = c.getString(3);
                r.precio     = c.getDouble(4);
                return r;
            }
            return null;
        } finally {
            c.close();
        }
    }
}
