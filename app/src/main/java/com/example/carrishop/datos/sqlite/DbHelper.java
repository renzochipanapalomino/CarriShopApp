package com.example.carrishop.datos.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DbHelper extends SQLiteOpenHelper {

    // ¡Nombre distinto a Room para que no borre tus usuarios!
    public static final String DB_NAME = "catalogo.db";
    public static final int DB_VERSION = 7; // subir versión para recrear si es necesario

    private static final String TAG = "DbHelper";

    private final Context context;

    public DbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        this.context = ctx.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        prepararListaMercado(db);
        verificarDatosIniciales(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        prepararListaMercado(db);
        verificarDatosIniciales(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT NOT NULL, " +
                "nombre_normalizado TEXT NOT NULL, " +
                "marca TEXT, " +
                "categoria TEXT)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_prod_norm ON productos(nombre_normalizado)");

        db.execSQL("CREATE TABLE IF NOT EXISTS supermercados (" +
                "id INTEGER PRIMARY KEY, " +
                "nombre TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS precios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "producto_id INTEGER NOT NULL, " +
                "supermercado_id INTEGER NOT NULL, " +
                "precio REAL NOT NULL, " +
                "FOREIGN KEY(producto_id) REFERENCES productos(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(supermercado_id) REFERENCES supermercados(id) ON DELETE CASCADE)");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_precio_prod_super ON precios(producto_id,supermercado_id)");

        prepararListaMercado(db);
        db.execSQL("CREATE TABLE IF NOT EXISTS lista_mercado (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "nombre_normalizado TEXT NOT NULL, " +
                "precio REAL DEFAULT 0, " +
                "cantidad INTEGER DEFAULT 0, " +
                "estado INTEGER DEFAULT 0, " +
                "agregado_por_voz INTEGER DEFAULT 0, " +
                "supermercado_id INTEGER DEFAULT -1");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_lista_norm ON lista_mercado(nombre_normalizado, supermercado_id)");

        ejecutarSeed(db, "sql/seed.sql");
        verificarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS precios");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS supermercados");
        db.execSQL("DROP TABLE IF EXISTS lista_mercado");
        onCreate(db);
    }

    private void prepararListaMercado(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS lista_mercado (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "nombre_normalizado TEXT NOT NULL, " +
                    "precio REAL DEFAULT 0, " +
                    "cantidad INTEGER DEFAULT 0, " +
                    "estado INTEGER DEFAULT 0, " +
                    "agregado_por_voz INTEGER DEFAULT 0, " +
                    "supermercado_id INTEGER DEFAULT -1" +
                    ")");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_lista_norm ON lista_mercado(nombre_normalizado, supermercado_id)");

            asegurarColumna(db, "lista_mercado", "agregado_por_voz", "INTEGER DEFAULT 0");
            asegurarColumna(db, "lista_mercado", "supermercado_id", "INTEGER DEFAULT -1");
        } catch (SQLiteException e) {
            Log.e(TAG, "No se pudo preparar la tabla lista_mercado", e);
        }
    }

    private void asegurarColumna(SQLiteDatabase db, String tabla, String columna, String definicion) {
        if (!tieneColumna(db, tabla, columna)) {
            try {
                db.execSQL("ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + definicion);
            } catch (SQLiteException e) {
                Log.e(TAG, "No se pudo agregar la columna " + columna + " en " + tabla, e);
            }
        }
    }

    private boolean tieneColumna(SQLiteDatabase db, String tabla, String columna) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + tabla + ")", null);
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(1);
                if (columna.equalsIgnoreCase(nombre)) {
                    return true;
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "No se pudo consultar PRAGMA table_info para " + tabla, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

    private void ejecutarSeed(SQLiteDatabase db, String assetPath) {
        try {
            InputStream in = context.getAssets().open(assetPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder stmt = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                String l = line.trim();
                if (l.isEmpty() || l.startsWith("--") || l.startsWith("//") || l.startsWith("/*")) continue;
                stmt.append(line).append('\n');
                if (l.endsWith(";")) {
                    db.execSQL(stmt.toString());
                    stmt.setLength(0);
                }
            }
            br.close();
        } catch (Exception ignore) {
            // si no existe el seed, continuar
        }
    }

    private void verificarDatosIniciales(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM productos", null);
            if (cursor.moveToFirst()) {
                long total = cursor.getLong(0);
                if (total == 0) {
                    ejecutarSeed(db, "sql/seed.sql");
                }
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "No se pudo verificar datos iniciales", e);
            ejecutarSeed(db, "sql/seed.sql");
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
