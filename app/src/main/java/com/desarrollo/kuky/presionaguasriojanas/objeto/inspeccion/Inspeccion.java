package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class Inspeccion {
    private int id;
    private String idUsuario;
    private Cliente cliente;
    private TipoInmueble tipoInmueble;
    private DestinoInmueble destinoInmueble;
    private TipoServicio tipoServicio;
    private boolean servicioCloacal;
    private float coeficienteZonal;
    private double latitud;
    private double longitud;
    private double latitudUsuario;
    private double longitudUsuario;
    private String observaciones;

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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoInmueble getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public DestinoInmueble getDestinoInmueble() {
        return destinoInmueble;
    }

    public void setDestinoInmueble(DestinoInmueble destinoInmueble) {
        this.destinoInmueble = destinoInmueble;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
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
}
