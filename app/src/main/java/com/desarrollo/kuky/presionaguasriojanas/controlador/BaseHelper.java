package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kuky on 22/05/2019.
 */
public class BaseHelper extends SQLiteOpenHelper {
    private static BaseHelper sInstance;

    private static final String DATABASE_NAME = "PresionAguasRiojanas";
    private static final int DATABASE_VERSION = 1;

    public static synchronized BaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new BaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private BaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(sqlTablaPuntosPresion);
        sqLiteDatabase.execSQL(sqlTablaUsuarios);
        sqLiteDatabase.execSQL(sqlTablaHistorialPuntosPresion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    String sqlTablaPuntosPresion = "CREATE TABLE IF NOT EXISTS `puntos_presion` (" +
            "  `id` int(11) NOT NULL," +
            "  `circuito` int(11) NOT NULL," +
            "  `barrio` varchar(30) NOT NULL," +
            "  `calle1` varchar(50) NOT NULL," +
            "  `calle2` varchar(50) DEFAULT NULL," +
            "  `latitud` varchar(30) NOT NULL," +
            "  `longitud` varchar(30) NOT NULL," +
            "  `pendiente` int(1) NOT NULL DEFAULT '1'," +
            "  `presion` float NOT NULL," +
            "  `id_tipo_presion` int(11) DEFAULT NULL," +
            "  `id_tipo_pendiente` int(11) DEFAULT '1'," +
            "  `id_tipo_punto` int(11) DEFAULT '1'," +
            "  PRIMARY KEY (`id`) " +
            ")";

    String sqlTablaHistorialPuntosPresion = "CREATE TABLE IF NOT EXISTS `historial_puntos_presion` (" +
            "  `id` int(11) NOT NULL," +
            "  `latitud` varchar(30) NOT NULL," +
            "  `longitud` varchar(30) NOT NULL," +
            "  `presion` float NOT NULL," +
            "  `id_punto_presion` int(11) DEFAULT NULL," +
            "  PRIMARY KEY (`id`) " +
            ")";

//    String sqlTablaPuntosPresion2 = "CREATE TABLE IF NOT EXISTS puntos_presion (" +
//            "id INT(11) default NULL," +
//            "circuito INT(11) default NULL," +
//            "barrio varchar(30) default NULL, " +
//            "calle1 varchar(50) default NULL," +
//            "calle2 varchar(50) default NULL," +
//            "perforacion varchar(10) default NULL," +
//            "latitud varchar(30) default NULL, " +
//            "longitud varchar(30) default NULL, " +
//            "id_tipo_presion INT(11) default NULL " +
//            ")";

    String sqlTablaUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
            "id INT(11) default NULL," +
            "nombre varchar(30) default NULL, " +
            "mail varchar(45) default NULL," +
            "clave varchar(45) default NULL" +
            ")";

    public String getSqlTablaUsuarios() {
        return sqlTablaUsuarios;
    }

    public String getSqlTablaPuntosPresion() {
        return sqlTablaPuntosPresion;
    }

    public String getSqlTablaHistorialPuntosPresion() {
        return sqlTablaHistorialPuntosPresion;
    }
}