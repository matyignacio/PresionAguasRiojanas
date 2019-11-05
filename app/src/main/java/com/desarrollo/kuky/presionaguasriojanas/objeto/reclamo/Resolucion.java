package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

import java.util.ArrayList;

public class Resolucion { // tabla GTresolucion
    private String resolucion;
    private String descripcion;
    // ACA DEFINO UN ARRAY LIST DE MOTIVOS COMO SI LA RELACION FUESE DE 1 A MUCHOS
    private ArrayList<MotivoTramite> motivos;

    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<MotivoTramite> getMotivos() {
        return motivos;
    }

    public void setMotivos(ArrayList<MotivoTramite> motivos) {
        this.motivos = motivos;
    }
}
