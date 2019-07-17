package com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion;

public class DatosRelevados {
    private int unidad;
    private boolean estado;
    private boolean medida;
    private int medidorAgua;
    private int medidorLuz;
    private int nis;

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public boolean isMedida() {
        return medida;
    }

    public void setMedida(boolean medida) {
        this.medida = medida;
    }

    public int getMedidorAgua() {
        return medidorAgua;
    }

    public void setMedidorAgua(int medidorAgua) {
        this.medidorAgua = medidorAgua;
    }

    public int getMedidorLuz() {
        return medidorLuz;
    }

    public void setMedidorLuz(int medidorLuz) {
        this.medidorLuz = medidorLuz;
    }

    public int getNis() {
        return nis;
    }

    public void setNis(int nis) {
        this.nis = nis;
    }
}
