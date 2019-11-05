package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

public class Tramite { // tabla GTtramite
    private ClienteTramite clienteTramite;
    private TipoTramite tipoTramite;
    private MotivoTramite motivoTramite;
    private String descripcion;

    public ClienteTramite getClienteTramite() {
        return clienteTramite;
    }

    public void setClienteTramite(ClienteTramite clienteTramite) {
        this.clienteTramite = clienteTramite;
    }

    public TipoTramite getTipoTramite() {
        return tipoTramite;
    }

    public void setTipoTramite(TipoTramite tipoTramite) {
        this.tipoTramite = tipoTramite;
    }

    public MotivoTramite getMotivoTramite() {
        return motivoTramite;
    }

    public void setMotivoTramite(MotivoTramite motivoTramite) {
        this.motivoTramite = motivoTramite;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
