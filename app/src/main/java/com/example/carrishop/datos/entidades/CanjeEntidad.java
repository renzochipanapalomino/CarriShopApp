package com.example.carrishop.datos.entidades;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "canjes")
public class CanjeEntidad {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int idUsuario;
    public String producto;
    public String codigo;
    public String fechaGeneracion;
    public String fechaExpiracion;
    public boolean canjeado = false;
}
