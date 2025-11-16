package com.example.carrishop.datos.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.carrishop.datos.modelos.ListaMercadoItem;
import com.example.carrishop.datos.util.TextoUtils;

import java.util.ArrayList;
import java.util.List;

public class ListaMercadoDao {

    private final DbHelper helper;

    public ListaMercadoDao(DbHelper helper) {
        this.helper = helper;
    }

    public List<ListaMercadoItem> obtenerTodos() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("lista_mercado", null, null, null, null, null, "id DESC");
        return leerLista(c);
    }

    public List<ListaMercadoItem> obtenerConPrecio(int superId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = "precio > 0 AND cantidad > 0";
        String[] args = null;
        if (superId > 0) {
            selection += " AND supermercado_id = ?";
            args = new String[]{ String.valueOf(superId) };
        }
        Cursor c = db.query("lista_mercado", null, selection, args, null, null, "id DESC");
        return leerLista(c);
    }

    public long insertarManual(String nombre, int superId) {
        if (nombre == null || nombre.trim().isEmpty()) return -1;
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
    }

    public void actualizarConPrecio(long id, double precio, int cantidad, boolean porVoz, int superId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("precio", precio);
        cv.put("cantidad", cantidad);
        cv.put("estado", 1);
        cv.put("agregado_por_voz", porVoz ? 1 : 0);
        if (superId > 0) cv.put("supermercado_id", superId);
        db.update("lista_mercado", cv, "id=?", new String[]{ String.valueOf(id) });
    }

    public long insertarDesdeVoz(String nombre, double precio, int cantidad, int superId) {
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
    }

    public ListaMercadoItem buscarPorId(long id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("lista_mercado", null, "id=?", new String[]{ String.valueOf(id) }, null, null, null);
        List<ListaMercadoItem> lista = leerLista(c);
        return lista.isEmpty() ? null : lista.get(0);
    }

    private long buscarIdPorNombre(String nombreNormalizado, int superId) {
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
    }

    public void eliminar(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("lista_mercado", "id=?", new String[]{ String.valueOf(id) });
    }

    public void eliminarPorNombre(String nombre) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("lista_mercado", "nombre_normalizado=?", new String[]{ TextoUtils.normalizar(nombre) });
    }

    public void limpiar() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("lista_mercado", null, null);
    }

    public void limpiarSincronizados() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("lista_mercado", "estado=1", null);
    }

    private List<ListaMercadoItem> leerLista(Cursor c) {
        List<ListaMercadoItem> datos = new ArrayList<>();
        if (c == null) return datos;
        try {
            while (c.moveToNext()) {
                ListaMercadoItem item = new ListaMercadoItem();
                item.id = c.getLong(c.getColumnIndexOrThrow("id"));
                item.nombre = c.getString(c.getColumnIndexOrThrow("nombre"));
                item.precio = c.getDouble(c.getColumnIndexOrThrow("precio"));
                item.cantidad = c.getInt(c.getColumnIndexOrThrow("cantidad"));
                item.estado = c.getInt(c.getColumnIndexOrThrow("estado"));
                item.agregadoPorVoz = c.getInt(c.getColumnIndexOrThrow("agregado_por_voz")) == 1;
                item.supermercadoId = c.getInt(c.getColumnIndexOrThrow("supermercado_id"));
                datos.add(item);
            }
        } finally {
            c.close();
        }
        return datos;
    }
}
