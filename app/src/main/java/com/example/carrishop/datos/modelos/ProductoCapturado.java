package com.example.carrishop.datos.modelos;

import java.io.Serializable;

public class ProductoCapturado implements Serializable {
    public String nombre;
    public double precio;
    public int cantidad;

    public ProductoCapturado(String nombre, double precio) {
        this(nombre, precio, 1);
    }

    public ProductoCapturado(String nombre, double precio, int cantidad) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }
}
