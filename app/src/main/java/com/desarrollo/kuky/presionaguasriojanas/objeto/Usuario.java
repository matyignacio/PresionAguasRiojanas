package com.desarrollo.kuky.presionaguasriojanas.objeto;

import java.util.ArrayList;

public class Usuario {

    private String usuario;
    private String nombre;
    private String email;
    private String clave;
    private String tipo;
    private String activo;
    private int banderaModuloPresion;
    /**
     * 0 ES QUE NUNCA INGRESO, DEBE SINCRONIZAR.
     * 1 YA INGRESO AL MENOS UNA VEZ
     */
    private int banderaSyncModuloPresion;

    /**
     * 0 ES QUE NO TIENE INFO NUEVA.
     * 1 YA INGRESO AL MENOS REGISTRO, DEBE SINCRONIZAR.
     */

    private int banderaModuloInspeccion;
    /**
     * 0 ES QUE NUNCA INGRESO, DEBE SINCRONIZAR.
     * 1 YA INGRESO AL MENOS UNA VEZ
     */
    private int banderaSyncModuloInspeccion;

    /**
     * 0 ES QUE NO TIENE INFO NUEVA.
     * 1 YA INGRESO AL MENOS REGISTRO, DEBE SINCRONIZAR.
     */

    private ArrayList<Modulo> modulos;

    private ArrayList<Integer> circuitos;

    public String getId() {
        return usuario;
    }

    public void setId(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public int getBanderaModuloPresion() {
        return banderaModuloPresion;
    }

    public void setBanderaModuloPresion(int banderaModuloPresion) {
        this.banderaModuloPresion = banderaModuloPresion;
    }

    public int getBanderaSyncModuloPresion() {
        return banderaSyncModuloPresion;
    }

    public void setBanderaSyncModuloPresion(int banderaSyncModuloPresion) {
        this.banderaSyncModuloPresion = banderaSyncModuloPresion;
    }

    public int getBanderaModuloInspeccion() {
        return banderaModuloInspeccion;
    }

    public void setBanderaModuloInspeccion(int banderaModuloInspeccion) {
        this.banderaModuloInspeccion = banderaModuloInspeccion;
    }

    public int getBanderaSyncModuloInspeccion() {
        return banderaSyncModuloInspeccion;
    }

    public void setBanderaSyncModuloInspeccion(int banderaSyncModuloInspeccion) {
        this.banderaSyncModuloInspeccion = banderaSyncModuloInspeccion;
    }

    public ArrayList<Modulo> getModulos() {
        return modulos;
    }

    public void setModulos(ArrayList<Modulo> modulos) {
        this.modulos = modulos;
    }

    public ArrayList<Integer> getCircuitos() {
        return circuitos;
    }

    public void setCircuitos(ArrayList<Integer> circuitos) {
        this.circuitos = circuitos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
