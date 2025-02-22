package com.desarrollo.kuky.presionaguasriojanas.objeto.presion;

import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;

import java.sql.Timestamp;

public class HistorialPuntos {
    private Integer id;
    private Double latitud;
    private Double longitud;
    private Float presion;
    private Timestamp fecha;
    private PuntoPresion puntoPresion;
    private Usuario usuario;
    private float cloro;
    private String muestra;

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

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public PuntoPresion getPuntoPresion() {
        return puntoPresion;
    }

    public void setPuntoPresion(PuntoPresion puntoPresion) {
        this.puntoPresion = puntoPresion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public float getCloro() {
        return cloro;
    }

    public void setCloro(float cloro) {
        this.cloro = cloro;
    }

    public String getMuestra() {
        return muestra;
    }

    public void setMuestra(String muestra) {
        this.muestra = muestra;
    }
}
