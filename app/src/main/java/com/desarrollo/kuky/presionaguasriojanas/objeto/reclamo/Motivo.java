package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

public class Motivo { // tabla GTmot_req
    private TipoTramite tipoTramite;
    private String motivo;
    private String descripcion;

    public Motivo() {
    }

    public Motivo(String motivo) {
        this.motivo = motivo;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
