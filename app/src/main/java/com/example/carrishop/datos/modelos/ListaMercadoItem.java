package com.example.carrishop.datos.modelos;

public class ListaMercadoItem {
    public long id;
    public String nombre;
    public double precio;
    public int cantidad;
    public boolean agregadoPorVoz;
    public int estado; // 0 = pendiente, 1 = sincronizado con lista de productos
    public int supermercadoId;

    public boolean tienePrecio() {
        return precio > 0 && cantidad > 0;
    }
}
