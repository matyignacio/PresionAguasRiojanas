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
        sqLiteDatabase.execSQL(sqlTablaTipoPunto);
        sqLiteDatabase.execSQL(sqlTablaOrden);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    String sqlTablaPuntosPresion = "CREATE TABLE IF NOT EXISTS `puntos_presion` (" +
            "  `id` INTEGER NOT NULL," +
            "  `circuito` int(11) NOT NULL," +
            "  `barrio` varchar(30) NOT NULL," +
            "  `calle1` varchar(50) NOT NULL," +
            "  `calle2` varchar(50) DEFAULT NULL," +
            "  `latitud` double NOT NULL," +
            "  `longitud` double NOT NULL," +
            "  `pendiente` int(1) NOT NULL DEFAULT '1'," +
            "  `presion` float NOT NULL," +
            "  `id_tipo_presion` int(11) DEFAULT NULL," +
            "  `id_tipo_punto` int(11) DEFAULT '1'," +
            "  `id_usuario` char(10) DEFAULT NULL," +
            "  `unidad` int(20) DEFAULT NULL"
            + ")";

    String sqlTablaHistorialPuntosPresion = "CREATE TABLE IF NOT EXISTS `historial_puntos_presion` (" +
            "  `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  `latitud` double NOT NULL," +
            "  `longitud` double NOT NULL," +
            "  `pendiente` int(1) NOT NULL DEFAULT '1'," +
            "  `presion` float NOT NULL," +
            "  `fecha` timestamp DEFAULT (datetime('now','localtime'))," +
            "  `id_punto_presion` int(11) DEFAULT NULL," +
            "  `id_usuario` char(10) DEFAULT NULL," +
            "  `id_usuario_historial` char(10) DEFAULT NULL"
            + ")";

    String sqlTablaUsuarios = "CREATE TABLE susuario (" +
            "  usuario char(10) NOT NULL DEFAULT ''," +
            "  nombre char(50) NOT NULL DEFAULT ''," +
            "  email varchar(100) DEFAULT ''," +
            "  clave char(10) NOT NULL DEFAULT ''," +
            "  tipo char(1) NOT NULL DEFAULT ''," +
            "  activo char(1) NOT NULL DEFAULT ''," +
            "  bandera_modulo_presion INTEGER default 0," +
            "  bandera_sync_modulo_presion INTEGER default 0," +
            "  PRIMARY KEY (`usuario`)" + ")";

    String sqlTablaTipoPunto = "CREATE TABLE `tipo_punto` (" +
            "  `id` int(11) NOT NULL," +
            "  `nombre` varchar(20) NOT NULL," +
            "  PRIMARY KEY (`id`)" + ") ";

    String sqlTablaOrden = "CREATE TABLE `orden` (" +
            "  `id` INTEGER PRIMARY KEY AUTOINCREMENT," +
            "  `id_pp_actual` int(11) DEFAULT NULL," +
            "  `id_usuario_pp_actual` char(10) DEFAULT NULL," +
            "  `id_pp_siguiente` int(11) DEFAULT NULL," +
            "  `id_usuario_pp_siguiente` char(10) DEFAULT NULL," +
            "  `activo` int(1) NOT NULL DEFAULT '0'" +
            " )";

    public String getSqlTablaUsuarios() {
        return sqlTablaUsuarios;
    }

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

    public static BaseHelper getsInstance() {
        return sInstance;
    }
}