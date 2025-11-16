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

    /**
     * Obtiene todos los ítems de la lista de mercado.
     */
    public List<ListaMercadoItem> obtenerTodos() {
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor c = db.query("lista_mercado", null, null, null, null, null, "id DESC")) {
            return leerLista(c);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error leyendo lista_mercado", e);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene los ítems que ya tienen un precio y cantidad asignados.
     * Opcionalmente, filtra por supermercado.
     */
    public List<ListaMercadoItem> obtenerConPrecio(int superId) {
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            String selection = "precio > 0 AND cantidad > 0";
            String[] args = null;
            if (superId > 0) {
                selection += " AND supermercado_id = ?";
                args = new String[]{String.valueOf(superId)};
            }
            try (Cursor c = db.query("lista_mercado", null, selection, args, null, null, "id DESC")) {
                return leerLista(c);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error leyendo lista con precio", e);
            return new ArrayList<>();
        }
    }

    /**
     * Inserta un nuevo ítem añadido manualmente (sin precio ni cantidad).
     * Si ya existe, actualiza sus datos básicos.
     */
    public long insertarManual(String nombre, int superId) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return -1;
        }
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            String normalizado = TextoUtils.normalizar(nombre);
            long existenteId = buscarIdPorNombre(normalizado, superId);

            ContentValues cv = new ContentValues();
            cv.put("nombre", nombre.trim());
            cv.put("nombre_normalizado", normalizado);
            cv.put("precio", 0);
            cv.put("cantidad", 0);
            cv.put("estado", 0); // 0 = pendiente
            cv.put("agregado_por_voz", 0);
            cv.put("supermercado_id", superId);

            if (existenteId > 0) {
                db.update("lista_mercado", cv, "id=?", new String[]{String.valueOf(existenteId)});
                return existenteId;
            } else {
                return db.insert("lista_mercado", null, cv);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error insertando manualmente", e);
            return -1;
        }
    }

    /**
     * Actualiza un ítem existente con su precio y cantidad.
     */
    public void actualizarConPrecio(long id, double precio, int cantidad, boolean porVoz, int superId) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            ContentValues cv = new ContentValues();
            cv.put("precio", precio);
            cv.put("cantidad", cantidad);
            cv.put("estado", 1); // 1 = sincronizado/agregado
            cv.put("agregado_por_voz", porVoz ? 1 : 0);
            if (superId > 0) {
                cv.put("supermercado_id", superId);
            }
            db.update("lista_mercado", cv, "id=?", new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error actualizando producto", e);
        }
    }

    /**
     * Inserta un ítem detectado por voz. Si ya existe, lo actualiza.
     */
    public long insertarDesdeVoz(String nombre, double precio, int cantidad, int superId) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            String normalizado = TextoUtils.normalizar(nombre);
            long existenteId = buscarIdPorNombre(normalizado, superId);

            ContentValues cv = new ContentValues();
            cv.put("nombre", nombre);
            cv.put("nombre_normalizado", normalizado);
            cv.put("precio", precio);
            cv.put("cantidad", cantidad);
            cv.put("estado", 1); // 1 = sincronizado/agregado
            cv.put("agregado_por_voz", 1);
            cv.put("supermercado_id", superId);

            if (existenteId > 0) {
                db.update("lista_mercado", cv, "id=?", new String[]{String.valueOf(existenteId)});
                return existenteId;
            } else {
                return db.insert("lista_mercado", null, cv);
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error insertando desde voz", e);
            return -1;
        }
    }

    /**
     * Busca y devuelve un solo ítem por su ID.
     */
    public ListaMercadoItem buscarPorId(long id) {
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor c = db.query("lista_mercado", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            List<ListaMercadoItem> lista = leerLista(c);
            return lista.isEmpty() ? null : lista.get(0);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error buscando item por id", e);
            return null;
        }
    }

    /**
     * Busca el ID de un ítem por su nombre normalizado.
     */
    private long buscarIdPorNombre(String nombreNormalizado, int superId) {
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            String selection = "nombre_normalizado = ?";
            List<String> args = new ArrayList<>();
            args.add(nombreNormalizado);

            if (superId > 0) {
                selection += " AND supermercado_id = ?";
                args.add(String.valueOf(superId));
            }

            try (Cursor c = db.query("lista_mercado", new String[]{"id"}, selection, args.toArray(new String[0]), null, null, null)) {
                if (c.moveToFirst()) {
                    return c.getLong(0);
                }
                return -1;
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "Error buscando por nombre", e);
            return -1;
        }
    }

    public void eliminar(long id) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete("lista_mercado", "id=?", new String[]{String.valueOf(id)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error eliminando item", e);
        }
    }

    public void eliminarPorNombre(String nombre) {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete("lista_mercado", "nombre_normalizado=?", new String[]{TextoUtils.normalizar(nombre)});
        } catch (SQLiteException e) {
            Log.e(TAG, "Error eliminando por nombre", e);
        }
    }

    public void limpiar() {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete("lista_mercado", null, null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error limpiando lista", e);
        }
    }

    public void limpiarSincronizados() {
        try (SQLiteDatabase db = helper.getWritableDatabase()) {
            db.delete("lista_mercado", "estado=1", null);
        } catch (SQLiteException e) {
            Log.e(TAG, "Error limpiando sincronizados", e);
        }
    }

    /**
     * Método de utilidad para convertir un Cursor en una lista de objetos.
     */
    private List<ListaMercadoItem> leerLista(Cursor c) {
        List<ListaMercadoItem> datos = new ArrayList<>();
        if (c == null) {
            return datos;
        }
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
            if (c != null) {
                c.close();
            }
        }
        return datos;
    }
}
