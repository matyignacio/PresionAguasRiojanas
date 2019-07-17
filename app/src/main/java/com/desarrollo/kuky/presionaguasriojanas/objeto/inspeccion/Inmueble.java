package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class Inmueble {
    private String tipoInmueble;
    private String destino;
    private boolean servicioCloacal;
    private float coeficienteZonal;
    private String tipoServicio;

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public boolean isServicioCloacal() {
        return servicioCloacal;
    }

    public void setServicioCloacal(boolean servicioCloacal) {
        this.servicioCloacal = servicioCloacal;
    }

    public float getCoeficienteZonal() {
        return coeficienteZonal;
    }

    public void setCoeficienteZonal(float coeficienteZonal) {
        this.coeficienteZonal = coeficienteZonal;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
}
