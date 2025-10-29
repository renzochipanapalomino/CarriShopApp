package com.example.carrishop.datos.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.carrishop.datos.entidades.CompraEntidad;

import java.util.List;

@Dao
public interface CompraDao {

    @Insert
    void insertar(CompraEntidad compra);

    @Query("SELECT * FROM compras WHERE idUsuario = :idUsuario ORDER BY fechaHora DESC")
    List<CompraEntidad> listarPorUsuario(int idUsuario);

    @Query("SELECT * FROM compras WHERE fechaHora LIKE :mes || '%' AND idUsuario = :idUsuario")
    List<CompraEntidad> comprasPorMes(int idUsuario, String mes);
}
