package com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo;

import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Barrio;

public class ClienteTramite { // tabla GTreclamo
    private TipoTramite tipoTramite;
    private MotivoTramite motivoTramite;
    private int unidad;
    private String razonSocial;
    private Barrio barrio;
    private String calle;
    private int numeroCasa;
    private String datoComplementario;
    private int medidorAgua;
    private int medidorLuz;
    private int nis;

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

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public Barrio getBarrio() {
        return barrio;
    }

    public void setBarrio(Barrio barrio) {
        this.barrio = barrio;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(int numeroCasa) {
        this.numeroCasa = numeroCasa;
    }

    public String getDatoComplementario() {
        return datoComplementario;
    }

    public void setDatoComplementario(String datoComplementario) {
        this.datoComplementario = datoComplementario;
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
