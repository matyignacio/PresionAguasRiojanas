package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

import java.util.ArrayList;

public class Inspeccion {
    private Cliente cliente;
    private Inmueble inmueble;
    private ArrayList<DatosRelevados> datosRelevados;

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public ArrayList<DatosRelevados> getDatosRelevados() {
        return datosRelevados;
    }

    public void setDatosRelevados(ArrayList<DatosRelevados> datosRelevados) {
        this.datosRelevados = datosRelevados;
    }
}
