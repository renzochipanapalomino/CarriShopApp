package com.example.carrishop.datos.entidades;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "usuarios",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class UsuarioEntidad {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String email;
    public String contrasena;
    public String telefono;
    public String dni;

    // 💎 Indica si la cuenta es Premium
    public boolean esPremium = false;

    // ⭐ Nuevo campo: estrellas acumuladas
    public int estrellas = 0;
}
