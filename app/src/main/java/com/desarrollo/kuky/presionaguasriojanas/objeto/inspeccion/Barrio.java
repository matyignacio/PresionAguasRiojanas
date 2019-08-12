package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class Barrio {
    private String codigo;
    private String desCodigo;
    private String zona;

    public Barrio() {
    }

    public Barrio(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDesCodigo() {
        return desCodigo;
    }

    public void setDesCodigo(String desCodigo) {
        this.desCodigo = desCodigo;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }
}
