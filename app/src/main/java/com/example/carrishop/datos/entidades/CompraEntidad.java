package com.example.carrishop.datos.entidades;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "compras")
public class CompraEntidad {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idUsuario;
    public String fechaHora; // formato: "2025-10-20 18:42"
    public double total;
    public int cantidadProductos;
}
