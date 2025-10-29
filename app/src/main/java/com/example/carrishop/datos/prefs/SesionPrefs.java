package com.example.carrishop.datos.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.carrishop.datos.entidades.UsuarioEntidad;

public class SesionPrefs {

    private static final String PREFS = "sesion_prefs";
    private static final String K_EMAIL = "email";
    private static final String K_PASS = "pass";
    private static final String K_REC = "recordar";
    private static final String K_NOM = "nombre";
    private static final String K_ID = "idUsuario";
    private static final String K_PREMIUM = "es_premium";
    private static final String K_PUNTOS = "puntos";

    private final SharedPreferences sp;

    public SesionPrefs(Context ctx) {
        sp = ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // =========================================================
    // 🔐 DATOS DE SESIÓN
    // =========================================================
    public void saveFull(String nombre, String email, String pass, boolean recordar) {
        // No se borra K_PUNTOS ni K_PREMIUM al guardar sesión
        String nombreSeguro = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : "Usuario";
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(K_NOM, nombreSeguro);
        editor.putString(K_EMAIL, email == null ? "" : email);
        editor.putString(K_PASS, pass == null ? "" : pass);
        editor.putBoolean(K_REC, recordar);
        editor.apply();
    }

    public String getNombre() { return sp.getString(K_NOM, "Usuario"); }
    public String getEmail() { return sp.getString(K_EMAIL, ""); }
    public String getPassword() { return sp.getString(K_PASS, ""); }
    public boolean isRecordar() { return sp.getBoolean(K_REC, false); }

    // =========================================================
    // 💎 ESTADO PREMIUM
    // =========================================================
    public boolean isPremium() { return sp.getBoolean(K_PREMIUM, false); }
    public void setPremium(boolean valor) { sp.edit().putBoolean(K_PREMIUM, valor).apply(); }

    // =========================================================
    // ⭐ PUNTOS ACUMULADOS
    // =========================================================
    public int getPuntos() {
        return sp.getInt(K_PUNTOS, 0);
    }

    public void addPuntos(int cantidad) {
        int actuales = getPuntos();
        int nuevos = actuales + cantidad;

        sp.edit().putInt(K_PUNTOS, nuevos).apply();

        Log.d("CarriShop", "Puntos sumados: +" + cantidad + " → Total: " + nuevos);
    }

    public void resetPuntos() {
        sp.edit().putInt(K_PUNTOS, 0).apply();
        Log.d("CarriShop", "Puntos reiniciados a 0");
    }

    // =========================================================
    // 🆔 ID USUARIO
    // =========================================================
    public void setUsuarioId(int id) { sp.edit().putInt(K_ID, id).apply(); }
    public int obtenerUsuarioId() { return sp.getInt(K_ID, 0); }

    // =========================================================
    // 🧹 LIMPIEZA (sin borrar puntos ni Premium)
    // =========================================================
    public void clearAll() {
        int puntosPrevios = getPuntos();
        boolean esPremium = isPremium();

        sp.edit().clear().apply();

        // Restaurar beneficios Premium y puntos
        sp.edit()
                .putInt(K_PUNTOS, puntosPrevios)
                .putBoolean(K_PREMIUM, esPremium)
                .apply();

        Log.d("CarriShop", " Sesión limpiada pero se mantuvieron puntos=" + puntosPrevios + " y Premium=" + esPremium);
    }
}
