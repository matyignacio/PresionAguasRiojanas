package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.desarrollo.kuky.presionaguasriojanas.objeto.HistorialPuntos;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Orden;
import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.MapActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ACTUALIZAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SEGUNDO_INICIO_MODULO_PRESION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class HistorialPuntosControlador {

    private SyncMysqlToSqlite syncMysqlToSqlite;
    private SyncSqliteToMysql syncSqliteToMysql;
    private ArrayList<HistorialPuntos> historiales;
    private ProgressDialog pDialog;


    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<HistorialPuntos> historiales;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("2/6 - Enviando historial...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            historiales = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            historiales = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection(a);
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < historiales.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO `historial_puntos_presion` " +
                            "(`latitud`, `longitud`, `presion`, `fecha`, `id_punto_presion`, `id_usuario`, `id_usuario_historial`) " +
                            "VALUES " +
                            "('" + historiales.get(i).getLatitud() + "', " +
                            "'" + historiales.get(i).getLongitud() + "', " +
                            "'" + historiales.get(i).getPresion() + "', " +
                            "'" + historiales.get(i).getFecha() + "', " +
                            "'" + historiales.get(i).getPuntoPresion().getId() + "', " +
                            "'" + historiales.get(i).getPuntoPresion().getUsuario().getId() + "', " +
                            "'" + historiales.get(i).getUsuario().getId() + "');";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            UPDETEAMOS LA PRESION
                    //////////////////////////////////////////////////////////////////////////////////*/
                    consultaSql = "UPDATE `puntos_presion` " +
                            " SET `presion` = '" + historiales.get(i).getPresion() + "' " +
                            " WHERE `id` = " + historiales.get(i).getPuntoPresion().getId() +
                            " AND `id_usuario` like '" + historiales.get(i).getPuntoPresion().getUsuario().getId() + "' ;";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL HISTORIAL
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(historiales.get(i), a);
                    check++;
                }
                if (check == historiales.size()) {
                    return "EXITO";
                } else {
                    return "ERROR";
                }
            } catch (SQLException e) {
                try {
                    Log.e("MOSTRARMENSAJE:::", "Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("EXITO")) {
                //mostrarMensaje(a, "2/6 - Se enviaron los historiales con exito");
                TipoPuntoControlador tipoPuntoControlador = new TipoPuntoControlador();
                tipoPuntoControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkHistorialToMysql");
            }
        }
    }

    public int sincronizarDeSqliteToMysql(Activity a) {
        try {
            syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncSqliteToMysql HPC" + e.toString());
            return ERROR;
        }
    }

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("6/6 - Recibiendo historial...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncMysqlToSqlite(Activity a) {
            this.a = a;
            check = ERROR;
        }

        @Override
        protected String doInBackground(String... strings) {
            Connection conn;
            PreparedStatement ps;
            ResultSet rs;
            try {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection(a);
                String consultaSql = "SELECT * FROM historial_puntos_presion ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM historial_puntos_presion");
                while (rs.next()) {
                    String sql = "INSERT INTO `historial_puntos_presion`" +
                            "(`id`," +
                            "`latitud`," +
                            "`longitud`," +
                            "`pendiente`," +
                            "`presion`," +
                            "`fecha`," +
                            "`id_punto_presion`," +
                            "`id_usuario`," +
                            "`id_usuario_historial`)" +
                            "VALUES" +
                            "('" + rs.getInt(1) + "','" + // id
                            rs.getDouble(2) + "','" + // latitud
                            rs.getDouble(3) + "','" + // longitud
                            "0','" + // pendiente
                            rs.getFloat(4) + "','" + // presion
                            rs.getTimestamp(5) + "','" + // fecha
                            rs.getInt(6) + "','" + // id_tipo_presion
                            rs.getString(7) + "','" + // id_usuario
                            rs.getString(8) + "');"; // id_usuario_historial
                    db.execSQL(sql);
                }
                check++;
                if (check == EXITOSO) {
                    db.close();
                    rs.close();
                    ps.close();
                    conn.close();
                    return "EXITO";
                } else {
                    db.close();
                    rs.close();
                    ps.close();
                    conn.close();
                    return "ERROR";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            if (s.equals("EXITO")) {
                //mostrarMensaje(a, "6/6 - Se copio el historial con exito");
                mostrarMensaje(a, "Se sincronizo con exito!");
                UsuarioControlador usuarioControlador = new UsuarioControlador();
                if (LoginActivity.usuario.getBandera_modulo_presion() == PRIMER_INICIO_MODULO_PRESION) {
                    usuarioControlador.editarBanderaModuloPresion(a, SEGUNDO_INICIO_MODULO_PRESION);
                }
                usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_BAJA);
                abrirActivity(a, MapActivity.class);
            } else {
                mostrarMensaje(a, "Error en el checkHistorial");
            }
        }

    }

    public int sincronizarDeMysqlToSqlite(Activity a) {
        try {
            syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncMysqlToSqlite HPC" + e.toString());
            return ERROR;
        }
    }

    public ArrayList<HistorialPuntos> extraerTodosPorPunto(Activity a, int id, String usuario) {
        historiales = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT hp.`id`," +
                "    hp.`latitud`," +
                "    hp.`longitud`," +
                "    hp.`presion`," +
                "    hp.`fecha`," +
                "    hp.`id_punto_presion`," +
                "    hp.`id_usuario`," +
                "    hp.`id_usuario_historial`" +
                " FROM historial_puntos_presion AS hp, puntos_presion AS pp " +
                " WHERE pp.id=hp.id_punto_presion " +
                " AND pp.id_usuario = hp.id_usuario" +
                " AND pp.id = " + id +
                " AND pp.id_usuario like '" + usuario + "'" +
                " ORDER BY fecha DESC", null);
        while (c.moveToNext()) {
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            Usuario uPunto = new Usuario();
            Usuario uHistorial = new Usuario();
            historialPuntos.setId(c.getInt(0));
            historialPuntos.setLatitud(c.getDouble(1));
            historialPuntos.setLongitud(c.getDouble(2));
            historialPuntos.setPresion(c.getFloat(3));
            historialPuntos.setFecha(Timestamp.valueOf(c.getString(4)));
            puntoPresion.setId(c.getInt(5));
            uPunto.setId(c.getString(6));
            puntoPresion.setUsuario(uPunto);
            uHistorial.setId(c.getString(7));
            historialPuntos.setPuntoPresion(puntoPresion);
            historialPuntos.setUsuario(uHistorial);
            historiales.add(historialPuntos);
        }
        c.close();
        db.close();
        return historiales;
    }

    public ArrayList<HistorialPuntos> extraerTodosPendientes(Activity a) {
        historiales = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM historial_puntos_presion " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            HistorialPuntos historialPuntos = new HistorialPuntos();
            PuntoPresion puntoPresion = new PuntoPresion();
            Usuario uPunto = new Usuario();
            Usuario uHistorial = new Usuario();
            historialPuntos.setId(c.getInt(0));
            historialPuntos.setLatitud(c.getDouble(1));
            historialPuntos.setLongitud(c.getDouble(2));
            historialPuntos.setPresion(c.getFloat(4));
            historialPuntos.setFecha(Timestamp.valueOf(c.getString(5)));
            puntoPresion.setId(c.getInt(6));
            uPunto.setId(c.getString(7));
            puntoPresion.setUsuario(uPunto);
            historialPuntos.setPuntoPresion(puntoPresion);
            uHistorial.setId(c.getString(8));
            historialPuntos.setUsuario(uHistorial);
            historiales.add(historialPuntos);
        }
        c.close();
        db.close();
        return historiales;
    }

    public int insertar(HistorialPuntos historialPuntos, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO `historial_puntos_presion`" +
                    "(`latitud`," +
                    "`longitud`," +
                    "`pendiente`," +
                    "`presion`," +
                    "`id_punto_presion`," +
                    "`id_usuario`," +
                    "`id_usuario_historial`)" +
                    "VALUES" +
                    "('" + historialPuntos.getLatitud() + "','" + // latitud
                    historialPuntos.getLongitud() + "','" + // longitud
                    INSERTAR_PUNTO + "','" + // pendiente
                    historialPuntos.getPresion() + "','" + // presion
                    historialPuntos.getPuntoPresion().getId() + "','" + // id_punto_presion
                    historialPuntos.getPuntoPresion().getUsuario().getId() + "','" + // id_usuario
                    historialPuntos.getUsuario().getId() + "');";// id_usuario_historial
            db.execSQL(sql);
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            PuntoPresion puntoPresion = puntoPresionControlador.extraerPorIdYUsuario(a,
                    historialPuntos.getPuntoPresion().getId(),
                    historialPuntos.getPuntoPresion().getUsuario().getId());
            /**
             * EVALUAREMOS SI EL PUNTO ES UNO NUEVO SIN IMPACTAR EN LA BASE MYSQL
             * O SI YA ES UN PUNTO CONOCIDO Y SIMPLEMENTE SE LE AGREGO UNA NUEVA MEDICION
             */
            SQLiteDatabase db2 = BaseHelper.getInstance(a).getWritableDatabase();
            if (puntoPresion.getPendiente() == INSERTAR_PUNTO) {
                sql = "UPDATE puntos_presion" +
                        " SET presion = '" + historialPuntos.getPresion() + "', pendiente = " + INSERTAR_PUNTO +
                        " WHERE id=" + historialPuntos.getPuntoPresion().getId() +
                        " AND id_usuario LIKE '" + historialPuntos.getPuntoPresion().getUsuario().getId() + "'";

            } else {
                sql = "UPDATE puntos_presion" +
                        " SET presion = '" + historialPuntos.getPresion() + "', pendiente = " + ACTUALIZAR_PUNTO +
                        " WHERE id=" + historialPuntos.getPuntoPresion().getId() +
                        " AND id_usuario LIKE '" + historialPuntos.getPuntoPresion().getUsuario().getId() + "'";
            }
            db2.execSQL(sql);
            /** SUBIMOS LA BANDERA DE SYNC MODULO PRESION **/
            UsuarioControlador usuarioControlador = new UsuarioControlador();
            usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_ALTA);
            /** EVUALUAMOS SI HAY QUE CAMBIAR EL ORDEN **/
            Orden orden = new Orden();
            OrdenControlador ordenControlador = new OrdenControlador();
            orden = ordenControlador.existePunto(a, historialPuntos.getPuntoPresion());
            if (orden.getId() > 0) {
                /** SI EL PUNTO EXISTE EN LA TABLA DE ORDEN, LE BAJAMOS LA BANDERA **/
                ordenControlador.editarActivo(a,
                        orden.getPpActual().getId(),
                        orden.getPpActual().getUsuario().getId(),
                        BANDERA_BAJA);
                /** Y LE SUBIMOS LA BANDERA AL SIGUIENTE PUNTO **/
                ordenControlador.editarActivo(a,
                        orden.getPpSiguiente().getId(),
                        orden.getPpSiguiente().getUsuario().getId(),
                        BANDERA_ALTA);
            }
            /** CERRAMOS LAS CONEXIONES **/
            db.close();
            db2.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar HPC " + e.toString());
            return ERROR;
        }
    }

    public int insertar(PuntoPresion pp, Activity a, int id) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO `historial_puntos_presion`" +
                    "(`latitud`," +
                    "`longitud`," +
                    "`pendiente`," +
                    "`presion`," +
                    "`id_punto_presion`," +
                    "`id_usuario`," +
                    "`id_usuario_historial`)" +
                    "VALUES" +
                    "('" + pp.getLatitud() + "','" + // latitud
                    pp.getLongitud() + "','" + // longitud
                    INSERTAR_PUNTO + "','" + // pendiente
                    pp.getPresion() + "','" + // presion
                    id + "','" + // id_punto_presion
                    pp.getUsuario().getId() + "','" + // id_usuario
                    pp.getUsuario().getId() + "');";// id_usuario_historial
            db.execSQL(sql);
            /**
             * EVALUAREMOS SI EL PUNTO ES UNO NUEVO SIN IMPACTAR EN LA BASE MYSQL
             * O SI YA ES UN PUNTO CONOCIDO Y SIMPLEMENTE SE LE AGREGO UNA NUEVA MEDICION
             */
            SQLiteDatabase db2 = BaseHelper.getInstance(a).getWritableDatabase();
            sql = "UPDATE puntos_presion" +
                    " SET presion = '" + pp.getPresion() + "', pendiente = " + INSERTAR_PUNTO +
                    " WHERE id=" + pp.getId() +
                    " AND id_usuario LIKE '" + pp.getUsuario().getId() + "'";

            db2.execSQL(sql);
            db.close();
            db2.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar HPC + PPC " + e.toString());
            return ERROR;
        }
    }

    public int actualizarPendiente(HistorialPuntos historialPuntos, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE historial_puntos_presion " +
                    "SET presion = '" + historialPuntos.getPresion() + "', pendiente = 0 " +
                    " WHERE id=" + historialPuntos.getPuntoPresion().getId() +
                    " AND id_usuario LIKE '" + historialPuntos.getPuntoPresion().getUsuario().getId() + "'";
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente HPC " + e.toString());
            return ERROR;
        }
    }
}
