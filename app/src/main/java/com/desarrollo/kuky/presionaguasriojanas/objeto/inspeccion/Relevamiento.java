package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class Relevamiento {
    private int id;
    private String idUsuario;
    private String barrio;
    private String tipoInmueble;
    private String destinoInmueble;
    private boolean conexionVisible;
    private int medidorLuz;
    private int medidorAgua;
    private double latitud;
    private double longitud;
    private double latitudUsuario;
    private double longitudUsuario;
    private String observaciones;
    private byte[] foto;
//
//    public Relevamiento() {
//        this.id = 1;
//        this.idUsuario = "";
//        this.barrio = "";
//        this.tipoInmueble = "";
//        this.destinoInmueble = "";
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

    public String getDestinoInmueble() {
        return destinoInmueble;
    }

    public void setDestinoInmueble(String destinoInmueble) {
        this.destinoInmueble = destinoInmueble;
    }

    public boolean isConexionVisible() {
        return conexionVisible;
    }

    public void setConexionVisible(boolean conexionVisible) {
        this.conexionVisible = conexionVisible;
    }

    public int getMedidorLuz() {
        return medidorLuz;
    }

    public void setMedidorLuz(int medidorLuz) {
        this.medidorLuz = medidorLuz;
    }

    public int getMedidorAgua() {
        return medidorAgua;
    }

    public void setMedidorAgua(int medidorAgua) {
        this.medidorAgua = medidorAgua;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
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

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
