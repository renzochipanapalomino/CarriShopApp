package com.example.carrishop.datos.bd;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.carrishop.datos.dao.*;
import com.example.carrishop.datos.entidades.*;

@Database(
        entities = {
                UsuarioEntidad.class,
                SupermercadoEntidad.class,
                ProductoEntidad.class,
                CanjeEntidad.class,
                CompraEntidad.class // ✅ nueva tabla de historial Premium
        },
        version = 3, // ⬆️ incrementamos versión
        exportSchema = false
)
public abstract class BaseDeDatosApp extends RoomDatabase {

    private static volatile BaseDeDatosApp INSTANCIA;

    public abstract UsuarioDao usuarioDao();
    public abstract SupermercadoDao supermercadoDao();
    public abstract ProductoDao productoDao();
    public abstract CanjeDao canjeDao();
    public abstract CompraDao compraDao(); // ✅ nuevo DAO

    public static BaseDeDatosApp obtener(Context ctx) {
        if (INSTANCIA == null) {
            synchronized (BaseDeDatosApp.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    BaseDeDatosApp.class,
                                    "carrishop.db"
                            )
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCIA;
    }
}
