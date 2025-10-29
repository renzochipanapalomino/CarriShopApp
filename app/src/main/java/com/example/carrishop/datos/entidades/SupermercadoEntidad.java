package com.example.carrishop.datos.entidades;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "supermercados")
public class SupermercadoEntidad {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public String descripcion;
    public boolean favorito;
}
