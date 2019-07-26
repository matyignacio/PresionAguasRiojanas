package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class DestinoInmueble {
    private int id;
    private String nombre;

    public DestinoInmueble() {
    }

    public DestinoInmueble(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
