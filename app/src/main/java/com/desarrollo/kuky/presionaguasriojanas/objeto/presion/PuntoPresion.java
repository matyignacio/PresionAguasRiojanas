package com.desarrollo.kuky.presionaguasriojanas.objeto.presion;

import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;

public class PuntoPresion {
    private Integer id;
    private Integer circuito;
    private String barrio;
    private String calle1;
    private String calle2;
    private Double latitud;
    private Double longitud;
    private Integer pendiente;
    private Float presion;
    private TipoPresion tipoPresion;
    private TipoPunto tipoPunto;
    private Usuario usuario;
    private int unidad;
    private String tipoUnidad;
    private int unidad2;
    private String tipoUnidad2;
    private float cloro;
    private String muestra;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCircuito() {
        return circuito;
    }

    public void setCircuito(Integer circuito) {
        this.circuito = circuito;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getCalle1() {
        return calle1;
    }

    public void setCalle1(String calle1) {
        this.calle1 = calle1;
    }

    public String getCalle2() {
        return calle2;
    }

    public void setCalle2(String calle2) {
        this.calle2 = calle2;
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

    public Integer getPendiente() {
        return pendiente;
    }

    public void setPendiente(Integer pendiente) {
        this.pendiente = pendiente;
    }

    public Float getPresion() {
        return presion;
    }

    public void setPresion(Float presion) {
        this.presion = presion;
    }

    public TipoPresion getTipoPresion() {
        return tipoPresion;
    }

    public void setTipoPresion(TipoPresion tipoPresion) {
        this.tipoPresion = tipoPresion;
    }

    public TipoPunto getTipoPunto() {
        return tipoPunto;
    }

    public void setTipoPunto(TipoPunto tipoPunto) {
        this.tipoPunto = tipoPunto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public String getTipoUnidad() {
        return tipoUnidad;
    }

    public void setTipoUnidad(String tipoUnidad) {
        this.tipoUnidad = tipoUnidad;
    }

    public int getUnidad2() {
        return unidad2;
    }

    public void setUnidad2(int unidad2) {
        this.unidad2 = unidad2;
    }

    public String getTipoUnidad2() {
        return tipoUnidad2;
    }

    public void setTipoUnidad2(String tipoUnidad2) {
        this.tipoUnidad2 = tipoUnidad2;
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
