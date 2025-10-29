package com.example.carrishop.datos.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.carrishop.datos.entidades.UsuarioEntidad;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * BackupManager — Gestiona el respaldo persistente del usuario CarriShop
 * Guarda un JSON con los datos del usuario en:
 * /Download/carrishop_backup/
 * Esta carpeta NO se borra al desinstalar la app.
 */
public class BackupManager {

    private static final String FILE_NAME = "backup_usuario.json";

    /**
     * 🔹 Guarda los datos del usuario en un archivo JSON dentro de /Download/carrishop_backup/
     */
    public static void guardar(Context context, UsuarioEntidad usuario) {
        if (usuario == null) return;

        try {
            // 📂 Carpeta pública de descargas (permanece tras desinstalar)
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "carrishop_backup");

            if (!backupDir.exists()) backupDir.mkdirs();

            File file = new File(backupDir, FILE_NAME);

            // 🧩 Convertir usuario → JSON
            Gson gson = new Gson();
            FileWriter writer = new FileWriter(file);
            gson.toJson(usuario, writer);
            writer.flush();
            writer.close();

            Log.i("BackupManager", "✅ Respaldo guardado en: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("BackupManager", "❌ Error al guardar respaldo", e);
        }
    }

    /**
     * 🔹 Restaura el usuario desde el respaldo (si existe)
     */
    public static UsuarioEntidad restaurar(Context context) {
        try {
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "carrishop_backup");
            File file = new File(backupDir, FILE_NAME);

            if (!file.exists()) {
                Log.w("BackupManager", "⚠️ No se encontró respaldo persistente.");
                return null;
            }

            Gson gson = new Gson();
            FileReader reader = new FileReader(file);
            UsuarioEntidad usuario = gson.fromJson(reader, UsuarioEntidad.class);
            reader.close();

            Log.i("BackupManager", "Respaldo restaurado correctamente.");
            return usuario;
        } catch (Exception e) {
            Log.e("BackupManager", "Error al restaurar respaldo", e);
            return null;
        }
    }

    /**
     * 🔹 Elimina el respaldo manualmente (opcional)
     */
    public static void eliminar(Context context) {
        try {
            File backupDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "carrishop_backup");
            File file = new File(backupDir, FILE_NAME);

            if (file.exists() && file.delete()) {
                Log.i("BackupManager", " Respaldo eliminado correctamente.");
            }
        } catch (Exception e) {
            Log.e("BackupManager", "Error al eliminar respaldo", e);
        }
    }
}
