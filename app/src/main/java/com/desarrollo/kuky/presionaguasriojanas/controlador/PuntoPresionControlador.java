package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.objeto.Usuario;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ACTUALIZAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.MAPA_CLIENTES;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class PuntoPresionControlador {
    private SyncMysqlToSqlite syncMysqlToSqlite;
    private SyncSqliteToMysql syncSqliteToMysql;
    private ArrayList<PuntoPresion> puntosPresion;
    private ProgressDialog pDialog;

    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<PuntoPresion> puntosPresion;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("1/6 - Enviando puntos...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            puntosPresion = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            ArrayList<PuntoPresion> puntosPresionInsertar = extraerTodosPendientesInsertar(a);
            ArrayList<PuntoPresion> puntosPresionActualizar = extraerTodosPendientesActualizar(a);
            Connection conn;
            conn = Conexion.GetConnection(a);
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < puntosPresionInsertar.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO `puntos_presion`" +
                            "(`id`," +
                            "`circuito`," +
                            "`barrio`," +
                            "`calle1`," +
                            "`calle2`," +
                            "`latitud`," +
                            "`longitud`," +
                            "`presion`," +
                            "`id_tipo_presion`," +
                            "`id_tipo_punto`," +
                            "`id_usuario`," +
                            "`unidad`)" +
                            " VALUES " +
                            "('" + puntosPresionInsertar.get(i).getId() + "','" + //id
                            puntosPresionInsertar.get(i).getCircuito() + "','" + //circuito
                            puntosPresionInsertar.get(i).getBarrio() + "','" + //barrio
                            puntosPresionInsertar.get(i).getCalle1() + "','" + //calle1
                            puntosPresionInsertar.get(i).getCalle2() + "','" + //calle2
                            puntosPresionInsertar.get(i).getLatitud() + "','" + //latitud
                            puntosPresionInsertar.get(i).getLongitud() + "','" + //longitud
                            puntosPresionInsertar.get(i).getPresion() + "','" + //presion
                            puntosPresionInsertar.get(i).getTipoPresion().getId() + "','" + //tipo presion
                            puntosPresionInsertar.get(i).getTipoPunto().getId() + "','" + //tipo punto
                            LoginActivity.usuario.getId() + "','" + //id_usuario
                            puntosPresionInsertar.get(i).getUnidad() + "');"; //unidad
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL PUNTO
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(puntosPresionInsertar.get(i), a);
                    check++;
                }
                for (int i = 0; i < puntosPresionActualizar.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            UPDATEAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "UPDATE `puntos_presion` SET " +
                            "`presion` = " + puntosPresionActualizar.get(i).getPresion() +
                            " WHERE id = " + puntosPresionActualizar.get(i).getId() +
                            " AND id_usuario like '" + puntosPresionActualizar.get(i).getId() + "'";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL PUNTO
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(puntosPresionActualizar.get(i), a);
                    check++;
                }
                if (check == (puntosPresionActualizar.size() + puntosPresionInsertar.size())) {
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
                //mostrarMensaje(a, "1/6 - Se enviaron los puntos con exito");
                HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
                historialPuntosControlador.sincronizarDeSqliteToMysql(a);
            } else {
                mostrarMensaje(a, "Error en el checkPuntosToMysql");
            }
        }
    }

    public int sincronizarDeSqliteToMysql(Activity a) {
        try {
            syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncSqliteToMysql PPC" + e.toString());
            return Util.ERROR;
        }
    }

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("5/6 - Recibiendo puntos...");
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
                String consultaSql = "SELECT * FROM puntos_presion ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM puntos_presion");
                while (rs.next()) {
                    /* Y REGISTRAMOS TODOS DE NUEVO*/
                    String sql = "INSERT INTO puntos_presion" +
                            "(id," +
                            "circuito," +
                            "barrio," +
                            "calle1," +
                            "calle2," +
                            "latitud," +
                            "longitud," +
                            "pendiente," +
                            "presion," +
                            "id_tipo_presion," +
                            "id_tipo_punto," +
                            "id_usuario," +
                            "unidad)" +
                            "VALUES" +
                            "(" + rs.getInt(1) + ",'" + // id
                            rs.getInt(2) + "','" + // circuito
                            rs.getString(3) + "','" + // barrio
                            rs.getString(4) + "','" + // calle1
                            rs.getString(5) + "','" + // calle2
                            rs.getDouble(6) + "','" + // latitud
                            rs.getDouble(7) + "','" + // longitud
                            "0','" + // pendiente
                            rs.getFloat(8) + "','" + // presion
                            rs.getInt(9) + "','" + // id_tipo_presion
                            rs.getInt(10) + "','" + // id_tipo_punto
                            rs.getString(11) + "'," + // id_usuario
                            rs.getInt(12) + ");"; // unidad
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
                //mostrarMensaje(a, "5/6 - Se copiaron puntos con exito");
                HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
                historialPuntosControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkPuntoPresion");
            }
        }
    }

    public int sincronizarDeMysqlToSqlite(Activity a) {
        try {
            syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncMysqlToSqlite PPC" + e.toString());
            return Util.ERROR;
        }
    }

    public ArrayList<PuntoPresion> extraerTodos(Activity a) {
        /** Extrae todos los puntos */
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion", null);
        while (c.moveToNext()) {
            Usuario u = new Usuario();
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            puntoPresion.setId(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            u.setId(c.getString(11));
            puntoPresion.setUsuario(u);
            puntoPresion.setUnidad(c.getInt(12));
            puntosPresion.add(puntoPresion);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public ArrayList<PuntoPresion> extraerTodos(Activity a, int idTipoPunto) {
        /** Extrae todos los puntos que sean del tipo "idTipoPunto" */
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c;
        if (idTipoPunto == MAPA_CLIENTES) {
            /** SI BUSCAMOS LOS CLIENTES, QUE NO SEAN MAS ANTIGUOS QUE UNA SEMANA */
            c = db.rawQuery("SELECT pp.id, pp.circuito, pp.barrio, pp.calle1, pp.calle2, " +
                    " pp.latitud, pp.longitud, pp.pendiente, pp.presion, " +
                    " pp.id_tipo_presion, pp.id_tipo_punto, pp.id_usuario, pp.unidad " +
                    " FROM puntos_presion AS pp, historial_puntos_presion AS hp " +
                    " WHERE julianday('now') - julianday(hp.fecha) < 7.12510325247422" +
                    " AND pp.id = hp.id_punto_presion" +
                    " AND pp.id_usuario = hp.id_usuario" +
                    " AND id_tipo_punto = " + MAPA_CLIENTES + " " +
                    " GROUP BY pp.id, pp.id_usuario" +
                    " ORDER BY hp.fecha DESC", null);
        } else {
            /** SI NO BUSCAMOS CLIENTES, QUE TRAIGA TODOS LOS PUNTOS HISTORICOS */
            c = db.rawQuery("SELECT * FROM puntos_presion" +
                    " WHERE id_tipo_punto=" + idTipoPunto, null);
        }
        while (c.moveToNext()) {
            Usuario u = new Usuario();
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            puntoPresion.setId(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            u.setId(c.getString(11));
            puntoPresion.setUsuario(u);
            puntoPresion.setUnidad(c.getInt(12));
            puntosPresion.add(puntoPresion);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public ArrayList<PuntoPresion> extraerTodos(Activity a, int idTipoPunto, int circuito) {
        /** Extrae todos los puntos que sean del tipo "idTipoPunto" y pertenezcan al circuito seleccinado*/
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c;
        if (idTipoPunto == MAPA_CLIENTES) {
            /** SI BUSCAMOS LOS CLIENTES, QUE NO SEAN MAS ANTIGUOS QUE UNA SEMANA */
            c = db.rawQuery("SELECT pp.id, pp.circuito, pp.barrio, pp.calle1, pp.calle2, " +
                    " pp.latitud, pp.longitud, pp.pendiente, pp.presion, " +
                    " pp.id_tipo_presion, pp.id_tipo_punto, pp.id_usuario, pp.unidad " +
                    " FROM puntos_presion AS pp, historial_puntos_presion AS hp " +
                    " WHERE julianday('now') - julianday(hp.fecha) < 7.12510325247422" +
                    " AND pp.id = hp.id_punto_presion" +
                    " AND pp.id_usuario = hp.id_usuario" +
                    " AND id_tipo_punto = " + MAPA_CLIENTES + " " +
                    " AND id_tipo_punto = " + circuito + " " +
                    " GROUP BY pp.id, pp.id_usuario" +
                    " ORDER BY hp.fecha DESC", null);
        } else {
            /** SI NO BUSCAMOS CLIENTES, QUE TRAIGA TODOS LOS PUNTOS HISTORICOS */
            c = db.rawQuery("SELECT * FROM puntos_presion" +
                    " WHERE id_tipo_punto =" + idTipoPunto +
                    " AND circuito =" + circuito, null);
        }
        while (c.moveToNext()) {
            Usuario u = new Usuario();
            PuntoPresion puntoPresion = new PuntoPresion();
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            puntoPresion.setId(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            u.setId(c.getString(11));
            puntoPresion.setUsuario(u);
            puntoPresion.setUnidad(c.getInt(12));
            puntosPresion.add(puntoPresion);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public PuntoPresion extraerPorIdYUsuario(Activity a, int id, String usuario) {
        PuntoPresion puntoPresion = new PuntoPresion();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion where id =" + id + " " +
                "AND id_usuario like '" + usuario + "'", null);
        while (c.moveToNext()) {
            TipoPresion tipoPresion = new TipoPresion();
            TipoPunto tipoPunto = new TipoPunto();
            Usuario u = new Usuario();
            puntoPresion.setId(c.getInt(0));
            puntoPresion.setCircuito(c.getInt(1));
            puntoPresion.setBarrio(c.getString(2));
            puntoPresion.setCalle1(c.getString(3));
            puntoPresion.setCalle2(c.getString(4));
            puntoPresion.setLatitud(c.getDouble(5));
            puntoPresion.setLongitud(c.getDouble(6));
            puntoPresion.setPendiente(c.getInt(7));
            puntoPresion.setPresion(c.getFloat(8));
            tipoPresion.setId(c.getInt(9));
            puntoPresion.setTipoPresion(tipoPresion);
            tipoPunto.setId(c.getInt(10));
            puntoPresion.setTipoPunto(tipoPunto);
            u.setId(usuario);
            puntoPresion.setUsuario(u);
            puntoPresion.setUnidad(c.getInt(12));
        }
        c.close();
        db.close();
        return puntoPresion;
    }

    public int insertar(PuntoPresion puntoPresion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            int id = obtenerSiguienteId(a);
            String sql = "INSERT INTO `puntos_presion`" +
                    "(`id`," +
                    "`circuito`," +
                    "`barrio`," +
                    "`calle1`," +
                    "`calle2`," +
                    "`latitud`," +
                    "`longitud`," +
                    "`pendiente`," +
                    "`presion`," +
                    "`id_tipo_presion`," +
                    "`id_tipo_punto`," +
                    "`id_usuario`," +
                    "`unidad`)" +
                    "VALUES" +
                    "('" + id + "','" + // id
                    +puntoPresion.getCircuito() + "','" + // circuito
                    puntoPresion.getBarrio() + "','" + // barrio
                    puntoPresion.getCalle1() + "','" + // calle1
                    puntoPresion.getCalle2() + "','" + // calle2
                    puntoPresion.getLatitud() + "','" + // latitud
                    puntoPresion.getLongitud() + "','" + // longitud
                    INSERTAR_PUNTO + "','" + // pendiente
                    puntoPresion.getPresion() + "','" + // presion
                    puntoPresion.getTipoPresion().getId() + "','" + // tipo_presion
                    puntoPresion.getTipoPunto().getId() + "','" + // tipo_punto
                    LoginActivity.usuario.getId() + "'," + // id_usuario
                    puntoPresion.getUnidad() + ");"; // id_usuario
            db.execSQL(sql);
            /** SUBIMOS LA BANDERA DE SYNC MODULO PRESION **/
            UsuarioControlador usuarioControlador = new UsuarioControlador();
            usuarioControlador.editarBanderaSyncModuloPresion(a, BANDERA_ALTA);
            /** LE INSERTAMOS UN HISTORIAL AL PUNTO **/
            HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
            historialPuntosControlador.insertar(puntoPresion, a, id);
            /** CERRAMOS LAS CONEXIONES **/
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar PPC " + e.toString());
            return Util.ERROR;
        }
    }

    public ArrayList<PuntoPresion> extraerTodosPendientesInsertar(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion " +
                "WHERE pendiente = " + INSERTAR_PUNTO, null);
        while (c.moveToNext()) {
            PuntoPresion pp = new PuntoPresion();
            pp.setId(c.getInt(0));
            pp.setCircuito(c.getInt(1));
            pp.setBarrio(c.getString(2));
            pp.setCalle1(c.getString(3));
            pp.setCalle2(c.getString(4));
            pp.setLatitud(c.getDouble(5));
            pp.setLongitud(c.getDouble(6));
            pp.setPresion(c.getFloat(8));
            TipoPresion tipoPresion = new TipoPresion();
            tipoPresion.setId(c.getInt(9));
            pp.setTipoPresion(tipoPresion);
            TipoPunto tipoPunto = new TipoPunto();
            tipoPunto.setId(c.getInt(10));
            pp.setTipoPunto(tipoPunto);
            Usuario u = new Usuario();
            u.setId(c.getString(11));
            pp.setUsuario(u);
            pp.setUnidad(c.getInt(12));
            puntosPresion.add(pp);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public ArrayList<PuntoPresion> extraerTodosPendientesActualizar(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion " +
                "WHERE pendiente = " + ACTUALIZAR_PUNTO, null);
        while (c.moveToNext()) {
            PuntoPresion pp = new PuntoPresion();
            pp.setId(c.getInt(0));
            pp.setCircuito(c.getInt(1));
            pp.setBarrio(c.getString(2));
            pp.setCalle1(c.getString(3));
            pp.setCalle2(c.getString(4));
            pp.setLatitud(c.getDouble(5));
            pp.setLongitud(c.getDouble(6));
            pp.setPresion(c.getFloat(8));
            TipoPresion tipoPresion = new TipoPresion();
            tipoPresion.setId(c.getInt(9));
            pp.setTipoPresion(tipoPresion);
            TipoPunto tipoPunto = new TipoPunto();
            tipoPunto.setId(c.getInt(10));
            pp.setTipoPunto(tipoPunto);
            Usuario u = new Usuario();
            u.setId(c.getString(11));
            pp.setUsuario(u);
            pp.setUnidad(c.getInt(12));
            puntosPresion.add(pp);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public int actualizarPendiente(PuntoPresion puntoPresion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE puntos_presion" +
                    " SET pendiente = 0" +
                    " WHERE id=" + puntoPresion.getId() +
                    " AND id_usuario like '" + puntoPresion.getUsuario().getId() + "'";
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente PPC " + e.toString());
            return Util.ERROR;
        }
    }

    public int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM puntos_presion ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId PPC " + e.toString());
            return Util.ERROR;
        }
    }
}
