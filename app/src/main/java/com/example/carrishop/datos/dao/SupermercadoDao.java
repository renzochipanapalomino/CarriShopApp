package com.example.carrishop.datos.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.carrishop.datos.entidades.SupermercadoEntidad;

import java.util.List;

@Dao
public interface SupermercadoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarTodos(List<SupermercadoEntidad> lista);

    @Query("SELECT * FROM supermercados ORDER BY favorito DESC, nombre ASC")
    List<SupermercadoEntidad> listar();
}
