package com.example.carrishop.datos.modelos;

public class Producto {
    public int id;
    public String nombre;
    public double precio;
    public int imagenRes;   // R.drawable.*
    public boolean oferta;

    public Producto(int id, String nombre, double precio, int imagenRes, boolean oferta) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imagenRes = imagenRes;
        this.oferta = oferta;
    }
}
