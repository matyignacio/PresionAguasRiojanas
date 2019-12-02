package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

import java.util.Date;

public class ResolucionReclamo {
    private String tipoTramite;
    private int numeroTramite;
    private String codigoResolucion;
    private String descripcionResolucion;
    private String observaciones;
    private String usuario;
    private Date fechaDesde;
    private String horaDesde;
    private Date fechaHasta;
    private String horaHasta;

    public String getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(String tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public int getNumeroTramite() {
        return numeroTramite;
    }

    public void setNumeroTramite(int numeroTramite) {
        this.numeroTramite = numeroTramite;
    }

    public String getCodigoResolucion() {
        return codigoResolucion;
    }

    public void setCodigoResolucion(String codigoResolucion) {
        this.codigoResolucion = codigoResolucion;
    }

    public String getDescripcionResolucion() {
        return descripcionResolucion;
    }

    public void setDescripcionResolucion(String descripcionResolucion) {
        this.descripcionResolucion = descripcionResolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public String getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(String horaDesde) {
        this.horaDesde = horaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public String getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(String horaHasta) {
        this.horaHasta = horaHasta;
    }
}
