package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

import java.util.ArrayList;

public class MotivoTramite { // tabla GTmot_req
    private TipoTramite tipoTramite;
    private String motivo;
    private String descripcion;
    // ACA DEFINO UN ARRAY LIST DE MOTIVOS COMO SI LA RELACION FUESE DE 1 A MUCHOS
    private ArrayList<Resolucion> resolucions;

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

    public ArrayList<Resolucion> getResoluciones() {
        return resolucions;
    }

    public void setResoluciones(ArrayList<Resolucion> resolucions) {
        this.resolucions = resolucions;
    }
}
