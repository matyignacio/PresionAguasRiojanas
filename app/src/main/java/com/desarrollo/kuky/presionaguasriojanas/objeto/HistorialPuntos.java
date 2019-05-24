package com.desarrollo.kuky.presionaguasriojanas.objeto;

import java.util.Date;

public class HistorialPuntos {
    private Integer id;
    private Double latitud;
    private Double longitud;
    private Float presion;
    private Date fecha;
    private PuntoPresion puntoPresion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Float getPresion() {
        return presion;
    }

    public void setPresion(Float presion) {
        this.presion = presion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public PuntoPresion getPuntoPresion() {
        return puntoPresion;
    }

    public void setPuntoPresion(PuntoPresion puntoPresion) {
        this.puntoPresion = puntoPresion;
    }
}
