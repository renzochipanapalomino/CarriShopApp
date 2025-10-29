package com.example.carrishop.datos.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.carrishop.datos.entidades.UsuarioEntidad;

@Dao
public interface UsuarioDao {

    // ✅ REPLACE permite insertar incluso si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertar(UsuarioEntidad u);

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    UsuarioEntidad porEmail(String email);

    @Query("SELECT * FROM usuarios WHERE email = :email AND contrasena = :contrasena LIMIT 1")
    UsuarioEntidad login(String email, String contrasena);

    @Query("UPDATE usuarios SET contrasena = :nueva WHERE email = :email")
    int actualizarContrasena(String email, String nueva);

    @Query("UPDATE usuarios SET nombre = :nombre, telefono = :telefono WHERE email = :email")
    int actualizarPerfil(String email, String nombre, String telefono);
}
