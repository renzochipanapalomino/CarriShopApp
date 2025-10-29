package com.example.carrishop.datos.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.carrishop.datos.entidades.ProductoEntidad;

import java.util.List;

@Dao
public interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarTodos(List<ProductoEntidad> lista);

    @Query("SELECT * FROM productos WHERE idSupermercado = :idMarket ORDER BY nombre")
    List<ProductoEntidad> porSupermercado(int idMarket);

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :q || '%' ORDER BY nombre LIMIT 20")
    List<ProductoEntidad> buscarPorNombre(String q);

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    ProductoEntidad porId(int id);
}
