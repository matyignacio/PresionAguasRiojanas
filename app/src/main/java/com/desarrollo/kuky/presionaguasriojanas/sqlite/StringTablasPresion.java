package com.desarrollo.kuky.presionaguasriojanas.sqlite;

public class StringTablasPresion {
    private String sqlTablaPuntosPresion = "CREATE TABLE IF NOT EXISTS puntos_presion (" +
            "  id INTEGER NOT NULL," +
            "  circuito int(11) NOT NULL," +
            "  barrio varchar(30) NOT NULL," +
            "  calle1 varchar(50) NOT NULL," +
            "  calle2 varchar(50) DEFAULT NULL," +
            "  latitud double NOT NULL," +
            "  longitud double NOT NULL," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  presion float NOT NULL," +
            "  id_tipo_presion int(11) DEFAULT NULL," +
            "  id_tipo_punto int(11) DEFAULT '1'," +
            "  id_usuario char(10) DEFAULT NULL," +
            "  unidad int(20) DEFAULT NULL," +
            "  tipo_unidad varchar(30) DEFAULT NULL," +
            "  unidad2 int(20) DEFAULT NULL," +
            "  tipo_unidad2 varchar(30) DEFAULT NULL," +
            "  cloro float DEFAULT NULL," +
            "  muestra varchar(45) DEFAULT NULL"
            + ")";

    private String sqlTablaHistorialPuntosPresion = "CREATE TABLE IF NOT EXISTS historial_puntos_presion (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  latitud double NOT NULL," +
            "  longitud double NOT NULL," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  presion float NOT NULL," +
            "  fecha timestamp DEFAULT (datetime('now','localtime'))," +
            "  id_punto_presion int(11) DEFAULT NULL," +
            "  id_usuario char(10) DEFAULT NULL," +
            "  id_usuario_historial char(10) DEFAULT NULL," +
            "  cloro float DEFAULT NULL," +
            "  muestra varchar(45) DEFAULT NULL"
            + ")";

    private String sqlTablaTipoPunto = "CREATE TABLE IF NOT EXISTS tipo_punto (" +
            "  id int(11) NOT NULL," +
            "  nombre varchar(20) NOT NULL," +
            "  PRIMARY KEY (id)" + ") ";

    private String sqlTablaOrden = "CREATE TABLE IF NOT EXISTS orden (" +
            "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  id_pp_actual int(11) DEFAULT NULL," +
            "  id_usuario_pp_actual char(10) DEFAULT NULL," +
            "  id_pp_siguiente int(11) DEFAULT NULL," +
            "  id_usuario_pp_siguiente char(10) DEFAULT NULL," +
            "  activo int(1) NOT NULL DEFAULT '0'," +
            "  circuito INT(1) NOT NULL DEFAULT '1'" +
            " )";

    public String getSqlTablaPuntosPresion() {
        return sqlTablaPuntosPresion;
    }

    public String getSqlTablaHistorialPuntosPresion() {
        return sqlTablaHistorialPuntosPresion;
    }

    public String getSqlTablaTipoPunto() {
        return sqlTablaTipoPunto;
    }

    public String getSqlTablaOrden() {
        return sqlTablaOrden;
    }
}
