package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class TipoServicio {
    private int id;
    private String nombre;

    public TipoServicio() {
    }

    public TipoServicio(int id) {
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
