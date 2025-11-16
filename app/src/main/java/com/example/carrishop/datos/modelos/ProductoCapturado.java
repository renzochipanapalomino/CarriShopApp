package com.example.carrishop.datos.modelos;

import java.io.Serializable;

public class ProductoCapturado implements Serializable {
    public String nombre;
    public double precio;
    public int cantidad;
    public long listaId;

    public ProductoCapturado(String nombre, double precio) {
        this(nombre, precio, 1, -1);
    }

    public ProductoCapturado(String nombre, double precio, int cantidad) {
        this(nombre, precio, cantidad, -1);
    }

    public ProductoCapturado(String nombre, double precio, int cantidad, long listaId) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.listaId = listaId;
    }
}
