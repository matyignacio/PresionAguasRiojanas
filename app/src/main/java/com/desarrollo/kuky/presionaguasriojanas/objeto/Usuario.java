package com.desarrollo.kuky.presionaguasriojanas.objeto;

public class Usuario {

    private Integer id;
    private String nombre;
    private String mail;
    private String clave;
    private int bandera_modulo_presion; // CERO ES QUE NUNCA INGRESO, DEBE SINCRONIZAR. UNO YA INGRESO AL MENOS UNA VEZ

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
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
