package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class Cliente {
    private int id;
    private String idUsuario;
    private String razonSocial;
    private long telefono;
    private String direccion;
    private String barrio;
    private int unidad;
    private int tramite;
    private String serv;
    private int nis;
    private int medidorAgua;
    private boolean estado;
    private int medidorLuz;
    private String reclama;

    public Cliente() {
    }

    public Cliente(int id) {
        this.id = id;
    }

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

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public long getTelefono() {
        return telefono;
    }

    public void setTelefono(long telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public int getTramite() {
        return tramite;
    }

    public void setTramite(int tramite) {
        this.tramite = tramite;
    }

    public String getServ() {
        return serv;
    }

    public void setServ(String serv) {
        this.serv = serv;
    }

    public int getNis() {
        return nis;
    }

    public void setNis(int nis) {
        this.nis = nis;
    }

    public int getMedidorAgua() {
        return medidorAgua;
    }

    public void setMedidorAgua(int medidorAgua) {
        this.medidorAgua = medidorAgua;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getMedidorLuz() {
        return medidorLuz;
    }

    public void setMedidorLuz(int medidorLuz) {
        this.medidorLuz = medidorLuz;
    }

    public String getReclama() {
        return reclama;
    }

    public void setReclama(String reclama) {
        this.reclama = reclama;
    }
}
