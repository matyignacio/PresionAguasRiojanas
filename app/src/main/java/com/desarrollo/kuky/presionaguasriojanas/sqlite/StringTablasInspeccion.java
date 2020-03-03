package com.desarrollo.kuky.presionaguasriojanas.sqlite;

public class StringTablasInspeccion {

    private String sqlTablaBarrios = "CREATE TABLE IF NOT EXISTS barrios (" +
            "  codigo char(4) NOT NULL," +
            "  des_codigo char(40) NOT NULL," +
            "  zona char(2) NOT NULL," +
            "  PRIMARY KEY (codigo))";

    private String sqlTablaTipoInmueble = "CREATE TABLE IF NOT EXISTS tipo_inmueble (" +
            "  id int(11) NOT NULL," +
            "  nombre varchar(45) NOT NULL," +
            "  PRIMARY KEY (id)" + ") ";

    private String sqlTablaRelevamiento = "CREATE TABLE IF NOT EXISTS relevamiento (" +
            "  id int(11) NOT NULL," +
            "  id_usuario char(10) NOT NULL," +
            "  barrio char(40) DEFAULT NULL," +
            "  tipo_inmueble varchar(45) DEFAULT NULL," +
            "  rubro varchar(45) DEFAULT NULL," +
            "  conexion_visible tinyint(4) DEFAULT NULL," +
            "  medidor_luz int(11) DEFAULT NULL," +
            "  medidor_agua int(11) DEFAULT NULL," +
            "  latitud double DEFAULT NULL," +
            "  longitud double DEFAULT NULL," +
            "  latitud_usuario double DEFAULT NULL," +
            "  longitud_usuario double DEFAULT NULL," +
            "  observaciones longtext DEFAULT NULL," +
            "  foto VARCHAR(55) DEFAULT NULL," +
            "  fecha timestamp DEFAULT (datetime('now','localtime'))," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  PRIMARY KEY (id,id_usuario))";

    private String sqlTablaRelevamientoMedidores = "CREATE TABLE IF NOT EXISTS relevamiento_medidores (" +
            "  id int(11) NOT NULL," +
            "  id_usuario char(10) DEFAULT NULL," +
            "  numero int(11) DEFAULT NULL," +
            "  id_relevamiento int(11) DEFAULT NULL," +
            "  id_usuario_relevamiento char(10) DEFAULT NULL," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  PRIMARY KEY (id))";

    public String getSqlTablaBarrios() {
        return sqlTablaBarrios;
    }

    public String getSqlTablaTipoInmueble() {
        return sqlTablaTipoInmueble;
    }

    public String getSqlTablaRelevamiento() {
        return sqlTablaRelevamiento;
    }

    public String getSqlTablaRelevamientoMedidores() {
        return sqlTablaRelevamientoMedidores;
    }
}
