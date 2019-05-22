package com.desarrollo.kuky.presionaguasriojanas.objeto;

public class PuntoPresion {
    private Integer id;
    private Integer circuito;
    private String barrio;
    private String calle1;
    private String calle2;
    private String perforacion;
    private String latitud;
    private String longitud;
    private TipoPresion tipoPresion;

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

    public String getPerforacion() {
        return perforacion;
    }

    public void setPerforacion(String perforacion) {
        this.perforacion = perforacion;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public TipoPresion getTipoPresion() {
        return tipoPresion;
    }

    public void setTipoPresion(TipoPresion tipoPresion) {
        this.tipoPresion = tipoPresion;
    }
}
