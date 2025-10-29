package com.example.carrishop.datos.modelos;

import java.io.Serializable;

/**
 * Modelo que representa un producto junto con su precio actual en un supermercado.
 * Se usa tanto para mostrar resultados como para el proceso de selección por voz.
 */
public class ProductoConPrecio implements Serializable {

    // Identificador del producto en la base de datos
    public long productoId;

    // Atributos principales
    public String nombre;
    public String marca;
    public String categoria;

    // Precio actual del producto
    public double precio;

    // Nueva propiedad: cantidad seleccionada por el usuario
    public int cantidad;

    // =============================
    // Constructores
    // =============================
    public ProductoConPrecio() {
        this.cantidad = 1; // valor por defecto
    }

    public ProductoConPrecio(long productoId, String nombre, String marca,
                             String categoria, double precio, int cantidad) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    // =============================
    // Métodos auxiliares
    // =============================

    /** Devuelve el subtotal calculado (precio × cantidad) */
    public double getSubtotal() {
        return precio * cantidad;
    }

    @Override
    public String toString() {
        return "ProductoConPrecio{" +
                "productoId=" + productoId +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                '}';
    }
}
