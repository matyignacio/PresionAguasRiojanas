package com.desarrollo.kuky.presionaguasriojanas.objeto;

public class Usuario {

    private String usuario;
    private String nombre;
    private String email;
    private String clave;
    private String tipo;
    private String activo;
    private int bandera_modulo_presion; // CERO ES QUE NUNCA INGRESO, DEBE SINCRONIZAR. UNO YA INGRESO AL MENOS UNA VEZ

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

    public int getBandera_modulo_presion() {
        return bandera_modulo_presion;
    }

    public void setBandera_modulo_presion(int bandera_modulo_presion) {
        this.bandera_modulo_presion = bandera_modulo_presion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
