package com.desarrollo.kuky.presionaguasriojanas.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kuky on 22/05/2019.
 */
public class BaseHelper extends SQLiteOpenHelper {
    private static BaseHelper sInstance;
    private StringTablasInspeccion tablasInspeccion = new StringTablasInspeccion();
    private StringTablasPresion tablasPresion = new StringTablasPresion();
    private StringTablasReclamo tablasReclamo = new StringTablasReclamo();
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

        sqLiteDatabase.execSQL(getSqlTablaPuntosPresion());
        sqLiteDatabase.execSQL(sqlTablaUsuarios);
        sqLiteDatabase.execSQL(getSqlTablaHistorialPuntosPresion());
        sqLiteDatabase.execSQL(getSqlTablaTipoPunto());
        sqLiteDatabase.execSQL(getSqlTablaOrden());
        sqLiteDatabase.execSQL(getSqlTablaTipoInmueble());
        sqLiteDatabase.execSQL(getSqlTablaBarrios());
        sqLiteDatabase.execSQL(getSqlTablaRelevamiento());
        sqLiteDatabase.execSQL(sqlTablaModulos);
        sqLiteDatabase.execSQL(getSqlTablaRelevamientoMedidores());
        sqLiteDatabase.execSQL(getSqlTablaTipoTramite());
        sqLiteDatabase.execSQL(getSqlTablaMotivoTramite());
        sqLiteDatabase.execSQL(getSqlTablaTipoResolucion());
        sqLiteDatabase.execSQL(getSqlTablaResolucionMotivos());
        sqLiteDatabase.execSQL(getSqlTablaReclamoTramite());
        sqLiteDatabase.execSQL(getSqlTablaTramite());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String dropTable(String table) {
        return "DROP TABLE IF EXISTS " + table;
    }


    private String sqlTablaUsuarios = "CREATE TABLE IF NOT EXISTS susuario (" +
            "  usuario char(10) NOT NULL DEFAULT ''," +
            "  nombre char(50) NOT NULL DEFAULT ''," +
            "  email varchar(100) DEFAULT ''," +
            "  clave char(10) NOT NULL DEFAULT ''," +
            "  tipo char(1) NOT NULL DEFAULT ''," +
            "  activo char(1) NOT NULL DEFAULT ''," +
            "  bandera_modulo_presion INTEGER default 0," +
            "  bandera_sync_modulo_presion INTEGER default 0," +
            "  bandera_modulo_inspeccion INTEGER default 0," +
            "  bandera_sync_modulo_inspeccion INTEGER default 0," +
            "  PRIMARY KEY (usuario)" + ")";

    private String sqlTablaModulos = "CREATE TABLE IF NOT EXISTS modulos (" +
            "  id int(11) NOT NULL," +
            "  nombre varchar(45) DEFAULT NULL," +
            "  PRIMARY KEY (id))";


    public String getSqlTablaUsuarios() {
        return sqlTablaUsuarios;
    }

    public String getSqlTablaPuntosPresion() {
        return tablasPresion.getSqlTablaPuntosPresion();
    }

    public String getSqlTablaHistorialPuntosPresion() {
        return tablasPresion.getSqlTablaHistorialPuntosPresion();
    }

    public String getSqlTablaTipoPunto() {
        return tablasPresion.getSqlTablaTipoPunto();
    }

    public String getSqlTablaOrden() {
        return tablasPresion.getSqlTablaOrden();
    }

    public String getSqlTablaTipoInmueble() {
        return tablasInspeccion.getSqlTablaTipoInmueble();
    }

    public String getSqlTablaBarrios() {
        return tablasInspeccion.getSqlTablaBarrios();
    }

    public String getSqlTablaRelevamiento() {
        return tablasInspeccion.getSqlTablaRelevamiento();
    }

    public String getSqlTablaModulos() {
        return sqlTablaModulos;
    }

    public String getSqlTablaRelevamientoMedidores() {
        return tablasInspeccion.getSqlTablaRelevamientoMedidores();
    }

    public String getSqlTablaTipoTramite() {
        return tablasReclamo.getSqlTablaTipoTramite();
    }

    public String getSqlTablaMotivoTramite() {
        return tablasReclamo.getSqlTablaMotivoTramite();
    }

    public String getSqlTablaTipoResolucion() {
        return tablasReclamo.getSqlTablaTipoResolucion();
    }

    public String getSqlTablaResolucionMotivos() {
        return tablasReclamo.getSqlTablaResolucionMotivos();
    }

    public String getSqlTablaReclamoTramite() {
        return tablasReclamo.getSqlTablaReclamoTramite();
    }

    public String getSqlTablaTramite() {
        return tablasReclamo.getSqlTablaTramite();
    }
}