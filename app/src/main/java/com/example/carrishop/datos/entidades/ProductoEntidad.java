package com.example.carrishop.datos.entidades;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "productos",
        indices = { @Index("nombre"), @Index("idSupermercado") }
)
public class ProductoEntidad {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String nombre;
    public double precio;
    public String imagenUrl;      // puede quedar vacío por ahora
    public String tags;           // palabras clave para sugerencias
    public int idSupermercado;    // relación al supermercado
}
