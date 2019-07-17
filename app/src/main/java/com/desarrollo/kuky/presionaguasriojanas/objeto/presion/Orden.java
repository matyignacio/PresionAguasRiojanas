package com.desarrollo.kuky.presionaguasriojanas.objeto.presion;

public class Orden {

    private int id;
    private PuntoPresion ppActual;
    private PuntoPresion ppSiguiente;
    private boolean activo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PuntoPresion getPpActual() {
        return ppActual;
    }

    public void setPpActual(PuntoPresion ppActual) {
        this.ppActual = ppActual;
    }

    public PuntoPresion getPpSiguiente() {
        return ppSiguiente;
    }

    public void setPpSiguiente(PuntoPresion ppSiguiente) {
        this.ppSiguiente = ppSiguiente;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
