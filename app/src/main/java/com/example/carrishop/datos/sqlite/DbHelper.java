package com.example.carrishop.datos.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DbHelper extends SQLiteOpenHelper {

    // ¡Nombre distinto a Room para que no borre tus usuarios!
    public static final String DB_NAME = "catalogo.db";
    public static final int DB_VERSION = 6; // subir versión para recrear si es necesario

    private final Context context;

    public DbHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
        this.context = ctx.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
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

        ejecutarSeed(db, "sql/seed.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS precios");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS supermercados");
        onCreate(db);
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
}
