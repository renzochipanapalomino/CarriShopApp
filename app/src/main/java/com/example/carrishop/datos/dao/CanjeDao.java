package com.example.carrishop.datos.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.carrishop.datos.entidades.CanjeEntidad;

import java.util.List;

@Dao
public interface CanjeDao {
    @Insert
    void insertar(CanjeEntidad canje);

    @Query("SELECT * FROM canjes WHERE idUsuario = :idUsuario ORDER BY id DESC")
    List<CanjeEntidad> obtenerPorUsuario(int idUsuario);

    @Query("SELECT * FROM canjes WHERE codigo = :codigo LIMIT 1")
    CanjeEntidad buscarPorCodigo(String codigo);
}
