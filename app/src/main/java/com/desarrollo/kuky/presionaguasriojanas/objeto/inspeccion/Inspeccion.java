package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class Inspeccion {
    private Cliente cliente;
    private TipoInmueble tipoInmueble;
    private DestinoInmueble destinoInmueble;
    private TipoServicio tipoServicio;
    private String observaciones;

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoInmueble getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public DestinoInmueble getDestinoInmueble() {
        return destinoInmueble;
    }

    public void setDestinoInmueble(DestinoInmueble destinoInmueble) {
        this.destinoInmueble = destinoInmueble;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
