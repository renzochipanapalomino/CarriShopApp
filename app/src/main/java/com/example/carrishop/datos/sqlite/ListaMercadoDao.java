package com.example.carrishop.datos.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.carrishop.datos.modelos.ListaMercadoItem;
import com.example.carrishop.datos.util.TextoUtils;

import java.util.ArrayList;
import java.util.List;

public class ListaMercadoDao {

    private final DbHelper helper;
    private static final String TAG = "ListaMercadoDao";

    public ListaMercadoDao(DbHelper helper) {
        this.helper = helper;
    }

    public List<ListaMercadoItem> obtenerTodos() {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query("lista_mercado", null, null, null, null, null, "id DESC");
            return leerLista(c);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error leyendo lista_mercado", e);
            return new ArrayList<>();
        }
    }

    public List<ListaMercadoItem> obtenerConPrecio(int superId) {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            String selection = "precio > 0 AND cantidad > 0";
            String[] args = null;
            if (superId > 0) {
                selection += " AND supermercado_id = ?";
                args = new String[]{ String.valueOf(superId) };
            }
            Cursor c = db.query("lista_mercado", null, selection, args, null, null, "id DESC");
            return leerLista(c);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error leyendo lista con precio", e);
            return new ArrayList<>();
        }
    }

    public long insertarManual(String nombre, int superId) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String normalizado = TextoUtils.normalizar(nombre);
            long existente = buscarIdPorNombre(normalizado, superId);

            ContentValues cv = new ContentValues();
            cv.put("nombre", nombre.trim());
            cv.put("nombre_normalizado", normalizado);
            cv.put("precio", 0);
            cv.put("cantidad", 0);
            cv.put("estado", 0);
            cv.put("agregado_por_voz", 0);
            cv.put("supermercado_id", superId);

            if (existente > 0) {
                db.update("lista_mercado", cv, "id=?", new String[]{ String.valueOf(existente) });
                return existente;
            }
            return db.insert("lista_mercado", null, cv);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error insertando manualmente", e);
            return -1;
        }
    }

    public void actualizarConPrecio(long id, double precio, int cantidad, boolean porVoz, int superId) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("precio", precio);
            cv.put("cantidad", cantidad);
            cv.put("estado", 1);
            cv.put("agregado_por_voz", porVoz ? 1 : 0);
            if (superId > 0) cv.put("supermercado_id", superId);
            db.update("lista_mercado", cv, "id=?", new String[]{ String.valueOf(id) });
        } catch (SQLiteException e) {
            Log.e(TAG, "Error actualizando producto", e);
        }
    }

    public long insertarDesdeVoz(String nombre, double precio, int cantidad, int superId) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            String normalizado = TextoUtils.normalizar(nombre);
            long existente = buscarIdPorNombre(normalizado, superId);

            ContentValues cv = new ContentValues();
            cv.put("nombre", nombre);
            cv.put("nombre_normalizado", normalizado);
            cv.put("precio", precio);
            cv.put("cantidad", cantidad);
            cv.put("estado", 1);
            cv.put("agregado_por_voz", 1);
            cv.put("supermercado_id", superId);

            if (existente > 0) {
                db.update("lista_mercado", cv, "id=?", new String[]{ String.valueOf(existente) });
                return existente;
            }
            return db.insert("lista_mercado", null, cv);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error insertando desde voz", e);
            return -1;
        }
    }

    public ListaMercadoItem buscarPorId(long id) {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor c = db.query("lista_mercado", null, "id=?", new String[]{ String.valueOf(id) }, null, null, null);
            List<ListaMercadoItem> lista = leerLista(c);
            return lista.isEmpty() ? null : lista.get(0);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error buscando item por id", e);
            return null;
        }
    }

    private long buscarIdPorNombre(String nombreNormalizado, int superId) {
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            String selection = "nombre_normalizado = ?";
            List<String> args = new ArrayList<>();
            args.add(nombreNormalizado);
            if (superId > 0) {
                selection += " AND supermercado_id = ?";
                args.add(String.valueOf(superId));
            }
            Cursor c = db.query("lista_mercado", new String[]{"id"}, selection,
                    args.toArray(new String[0]), null, null, null);
            try {
                if (c.moveToFirst()) {
                    return c.getLong(0);
                }
                return -1;
            } finally {
                c.close();
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error buscando por nombre", e);
            return -1;
        }
    }

    public void eliminar(long id) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("lista_mercado", "id=?", new String[]{ String.valueOf(id) });
        } catch (SQLiteException e) {
            Log.e(TAG, "Error eliminando item", e);
        }
    }

    public void eliminarPorNombre(String nombre) {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("lista_mercado", "nombre_normalizado=?", new String[]{ TextoUtils.normalizar(nombre) });
        } catch (SQLiteException e) {
            Log.e(TAG, "Error eliminando por nombre", e);
        }
    }

    public void limpiar() {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("lista_mercado", null, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error limpiando lista", e);
        }
    }

    public void limpiarSincronizados() {
        try {
            SQLiteDatabase db = helper.getWritableDatabase();
            db.delete("lista_mercado", "estado=1", null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error limpiando sincronizados", e);
        }
    }

    private List<ListaMercadoItem> leerLista(Cursor c) {
        List<ListaMercadoItem> datos = new ArrayList<>();
        if (c == null) return datos;
        try {
            while (c.moveToNext()) {
                int idxId = c.getColumnIndex("id");
                int idxNombre = c.getColumnIndex("nombre");
                if (idxId == -1 || idxNombre == -1) continue;

                ListaMercadoItem item = new ListaMercadoItem();
                item.id = c.getLong(idxId);
                item.nombre = c.getString(idxNombre);
                item.precio = leerDouble(c, "precio", 0);
                item.cantidad = leerInt(c, "cantidad", 0);
                item.estado = leerInt(c, "estado", 0);
                item.agregadoPorVoz = leerInt(c, "agregado_por_voz", 0) == 1;
                item.supermercadoId = leerInt(c, "supermercado_id", -1);
                datos.add(item);
            }
        } finally {
            c.close();
        }
        return datos;
    }

    private double leerDouble(Cursor c, String columna, double valorPorDefecto) {
        int idx = c.getColumnIndex(columna);
        return idx >= 0 ? c.getDouble(idx) : valorPorDefecto;
    }

    private int leerInt(Cursor c, String columna, int valorPorDefecto) {
        int idx = c.getColumnIndex(columna);
        return idx >= 0 ? c.getInt(idx) : valorPorDefecto;
    }
}
