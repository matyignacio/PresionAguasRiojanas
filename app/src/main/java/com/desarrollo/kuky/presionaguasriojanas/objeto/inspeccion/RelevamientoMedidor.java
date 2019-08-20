package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class RelevamientoMedidor {

    private int id;
    private String idUsuario;
    private int numero;
    private Relevamiento relevamiento;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Relevamiento getRelevamiento() {
        return relevamiento;
    }

    public void setRelevamiento(Relevamiento relevamiento) {
        this.relevamiento = relevamiento;
    }
}
