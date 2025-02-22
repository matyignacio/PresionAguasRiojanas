package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

import java.io.Serializable;
import java.util.Date;

public class Relevamiento implements Serializable {
    private int id;
    private String idUsuario;
    private String barrio;
    private String tipoInmueble;
    private String rubro;
    private boolean conexionVisible;
    private long medidorLuz;
    private String medidorAgua;
    private Double latitud;
    private Double longitud;
    private double latitudUsuario;
    private double longitudUsuario;
    private String observaciones;
    private String foto;
    private Date fecha;
//
//    public Relevamiento() {
//        this.id = 1;
//        this.idUsuario = "";
//        this.barrio = "";
//        this.tipoInmueble = "";
//        this.rubro = "";
//        this.conexionVisible = false;
//        this.medidorAgua = 0;
//        this.medidorLuz = 0;
//        this.latitud = 0;
//        this.longitud = 0;
//        this.latitudUsuario = 0;
//        this.longitudUsuario = 0;
//        this.observaciones = "";
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getRubro() {
        return rubro;
    }

    public void setRubro(String rubro) {
        this.rubro = rubro;
    }

    public boolean isConexionVisible() {
        return conexionVisible;
    }

    public void setConexionVisible(boolean conexionVisible) {
        this.conexionVisible = conexionVisible;
    }

    public long getMedidorLuz() {
        return medidorLuz;
    }

    public void setMedidorLuz(long medidorLuz) {
        this.medidorLuz = medidorLuz;
    }

    public String getMedidorAgua() {
        return medidorAgua;
    }

    public void setMedidorAgua(String medidorAgua) {
        this.medidorAgua = medidorAgua;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitudUsuario() {
        return latitudUsuario;
    }

    public void setLatitudUsuario(double latitudUsuario) {
        this.latitudUsuario = latitudUsuario;
    }

    public double getLongitudUsuario() {
        return longitudUsuario;
    }

    public void setLongitudUsuario(double longitudUsuario) {
        this.longitudUsuario = longitudUsuario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
