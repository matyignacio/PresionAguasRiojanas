package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

import java.util.ArrayList;
import java.util.List;

public class TipoResolucion { // tabla GTresolucion
    private String resolucion;
    private String descripcion;

    public TipoResolucion() {
    }

    public TipoResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

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

    public class TipoResolucionSpinner { // tabla GTresolucion
        private ArrayList<TipoResolucion> resoluciones;
        public List<String> labelsResoluciones = new ArrayList<>();

        public TipoResolucionSpinner() {
        }

        public ArrayList<TipoResolucion> getResoluciones() {
            return resoluciones;
        }

        public void setResoluciones(ArrayList<TipoResolucion> resoluciones) {
            this.resoluciones = resoluciones;
        }

        public List<String> getLabelsResoluciones() {
            return labelsResoluciones;
        }

        public void setLabelsResoluciones(List<String> labelsResoluciones) {
            this.labelsResoluciones = labelsResoluciones;
        }
    }
}
