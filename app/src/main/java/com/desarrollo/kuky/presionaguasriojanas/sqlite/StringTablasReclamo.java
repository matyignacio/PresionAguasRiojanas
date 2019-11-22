package com.desarrollo.kuky.presionaguasriojanas.sqlite;

public class StringTablasReclamo {

    private String sqlTablaTipoTramite = "CREATE TABLE GTtpo_tram (" +
            "  tpo_tram char(8) NOT NULL DEFAULT ''," +
            "  descripcion char(40) NOT NULL DEFAULT ''," +
            "  PRIMARY KEY (tpo_tram)" +
            ")";

    private String sqlTablaMotivoTramite = "CREATE TABLE GTmot_req (" +
            "  motivo char(8) NOT NULL DEFAULT ''," +
            "  descripcion char(40) NOT NULL DEFAULT ''," +
            "  tpo_tram char(8) NOT NULL DEFAULT ''," +
            "  PRIMARY KEY (motivo)" +
            ")";

    private String sqlTablaTipoResolucion = "CREATE TABLE GTresolucion (" +
            "  resolucion char(8) NOT NULL DEFAULT ''," +
            "  descripcion char(40) NOT NULL DEFAULT ''," +
            "  PRIMARY KEY (resolucion)" +
            ")";

    private String sqlTablaResolucionMotivos = "CREATE TABLE GTres_mot (" +
            "  resolucion char(8) NOT NULL DEFAULT ''," +
            "  motivo char(8) NOT NULL DEFAULT ''" +
            ")";

    private String sqlTablaReclamoTramite = "CREATE TABLE GTreclamo (" +
            "  tpo_tram varchar(8) DEFAULT ''," +
            "  num_tram int(11) DEFAULT NULL," +
            "  unidad_sol varchar(8) DEFAULT ''," +
            "  razon_sol varchar(40) DEFAULT ''," +
            "  calle varchar(40) DEFAULT ''," +
            "  numero double DEFAULT NULL," +
            "  dat_complem varchar(240) DEFAULT ''," +
            "  cod_barrio char(3) DEFAULT ''," +
            "  descripcion varchar(255) DEFAULT ''," +
            "  PRIMARY KEY (tpo_tram,num_tram)" +
            ")";

    private String sqlTablaTramite = "CREATE TABLE GTtramite (" +
            "  tpo_tram varchar(8) NOT NULL DEFAULT ''," +
            "  num_tram int(11) NOT NULL," +
            "  descripcion char(200) DEFAULT ''," +
            "  motivo char(8) NOT NULL DEFAULT ''," +
            "  PRIMARY KEY (tpo_tram,num_tram)" +
            ")";

    public String getSqlTablaTipoTramite() {
        return sqlTablaTipoTramite;
    }

    public String getSqlTablaMotivoTramite() {
        return sqlTablaMotivoTramite;
    }

    public String getSqlTablaTipoResolucion() {
        return sqlTablaTipoResolucion;
    }

    public String getSqlTablaResolucionMotivos() {
        return sqlTablaResolucionMotivos;
    }

    public String getSqlTablaReclamoTramite() {
        return sqlTablaReclamoTramite;
    }

    public String getSqlTablaTramite() {
        return sqlTablaTramite;
    }
}
