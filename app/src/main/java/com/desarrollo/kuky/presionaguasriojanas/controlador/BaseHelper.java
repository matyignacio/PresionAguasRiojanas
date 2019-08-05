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
        sqLiteDatabase.execSQL(sqlTablaTipoInmueble);
        sqLiteDatabase.execSQL(sqlTablaDestinoInmueble);
        sqLiteDatabase.execSQL(sqlTablaTipoServicio);
        sqLiteDatabase.execSQL(sqlTablaCliente);
        sqLiteDatabase.execSQL(sqlTablaInspeccion);
        sqLiteDatabase.execSQL(sqlTablaDatosRelevados);
        sqLiteDatabase.execSQL(sqlTablaBarrios);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String dropTable(String table) {
        return "DROP TABLE IF EXISTS " + table;
    }

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
            "  activo int(1) NOT NULL DEFAULT '0'" +
            " )";

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

    private String sqlTablaTipoInmueble = "CREATE TABLE IF NOT EXISTS tipo_inmueble (" +
            "  id int(11) NOT NULL," +
            "  nombre varchar(45) NOT NULL," +
            "  PRIMARY KEY (id)" + ") ";

    private String sqlTablaDestinoInmueble = "CREATE TABLE IF NOT EXISTS destino_inmueble (" +
            "  id int(11) NOT NULL," +
            "  nombre varchar(45) NOT NULL," +
            "  PRIMARY KEY (id)" + ") ";

    private String sqlTablaTipoServicio = "CREATE TABLE IF NOT EXISTS tipo_servicio (" +
            "  id int(11) NOT NULL," +
            "  nombre varchar(45) NOT NULL," +
            "  PRIMARY KEY (id)" + ") ";

    private String sqlTablaCliente = "CREATE TABLE IF NOT EXISTS cliente (" +
            "  id int(11) NOT NULL," +
            "  id_usuario char(10) NOT NULL," +
            "  razon_social varchar(45) NOT NULL," +
            "  direccion varchar(60) NOT NULL," +
            "  barrio varchar(30) NOT NULL," +
            "  telefono int(20) DEFAULT NULL," +
            "  unidad int(10) DEFAULT NULL," +
            "  nis int(10) DEFAULT NULL," +
            "  med_agua int(10) DEFAULT NULL," +
            "  med_luz int(10) DEFAULT NULL," +
            "  tramite int(15) DEFAULT NULL," +
            "  serv varchar(45) DEFAULT NULL," +
            "  estado tinyint(4) DEFAULT '0'," +
            "  reclama varchar(45) DEFAULT NULL," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  PRIMARY KEY (id,id_usuario) )";

    private String sqlTablaInspeccion = "CREATE TABLE IF NOT EXISTS inspeccion (" +
            "  id int(11) NOT NULL," +
            "  id_usuario char(10) NOT NULL," +
            "  id_cliente int(11) DEFAULT NULL," +
            "  id_usuario_cliente char(10) NOT NULL," +
            "  id_tipo_inmueble int(11) DEFAULT NULL," +
            "  id_destino_inmueble int(11) DEFAULT NULL," +
            "  id_tipo_servicio int(11) DEFAULT NULL," +
            "  servicio_cloacal tinyint(4) NOT NULL DEFAULT '0'," +
            "  coeficiente_zonal float NOT NULL DEFAULT '1'," +
            "  latitiud double NOT NULL," +
            "  longitud double NOT NULL," +
            "  latitud_usuario double NOT NULL," +
            "  longitud_usuario double NOT NULL," +
            "  observaciones longtext," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  PRIMARY KEY (id,id_usuario))";

    private String sqlTablaDatosRelevados = "CREATE TABLE IF NOT EXISTS datos_relevados (" +
            "  id int(11) NOT NULL," +
            "  id_usuario char(10) NOT NULL," +
            "  unidad int(10) NOT NULL," +
            "  estado tinyint(4) NOT NULL DEFAULT '0'," +
            "  medida tinyint(4) DEFAULT '0'," +
            "  med_agua int(10) DEFAULT NULL," +
            "  med_luz int(10) DEFAULT NULL," +
            "  nis int(10) DEFAULT NULL," +
            "  id_inpseccion int(11) DEFAULT NULL," +
            "  id_usuario_inspeccion char(10) DEFAULT NULL," +
            "  pendiente int(1) NOT NULL DEFAULT '1'," +
            "  PRIMARY KEY (id,id_usuario))";

    private String sqlTablaBarrios = "CREATE TABLE IF NOT EXISTS barrios (" +
            "  codigo char(4) NOT NULL," +
            "  des_codigo char(40) NOT NULL," +
            "  zona char(2) NOT NULL," +
            "  PRIMARY KEY (codigo))";

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

    public String getSqlTablaTipoInmueble() {
        return sqlTablaTipoInmueble;
    }

    public String getSqlTablaDestinoInmueble() {
        return sqlTablaDestinoInmueble;
    }

    public String getSqlTablaTipoServicio() {
        return sqlTablaTipoServicio;
    }

    public String getSqlTablaCliente() {
        return sqlTablaCliente;
    }

    public String getSqlTablaInspeccion() {
        return sqlTablaInspeccion;
    }

    public String getSqlTablaDatosRelevados() {
        return sqlTablaDatosRelevados;
    }

    public String getSqlTablaBarrios() {
        return sqlTablaBarrios;
    }
}