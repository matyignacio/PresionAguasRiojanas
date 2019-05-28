package com.desarrollo.kuky.presionaguasriojanas.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.objeto.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPresion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.TipoPunto;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PuntoPresionControlador {
    private SyncMysqlToSqlite syncMysqlToSqlite;
    private SyncSqliteToMysql syncSqliteToMysql;
    private Integer check;
    private ArrayList<PuntoPresion> puntosPresion;

    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private ProgressDialog pDialog;
        ArrayList<PuntoPresion> puntosPresion;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Enviando historial...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = 0;
            puntosPresion = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            puntosPresion = extraerTodosPendientes(a);
            Connection conn;
            try {
                conn = Conexion.GetConnection(a);
                String consultaSql;
                for (int i = 0; i < puntosPresion.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO `puntos_presion`\n" +
                            "(`circuito`," +
                            "`barrio`," +
                            "`calle1`," +
                            "`calle2`," +
                            "`latitud`," +
                            "`longitud`," +
                            "`presion`," +
                            "`id_tipo_presion`," +
                            "`id_tipo_punto`)" +
                            " VALUES " +
                            "('" + puntosPresion.get(i).getCircuito() + "','" + //circuito
                            puntosPresion.get(i).getBarrio() + "','" + //barrio
                            puntosPresion.get(i).getCalle1() + "','" + //calle1
                            puntosPresion.get(i).getCalle2() + "','" + //calle2
                            puntosPresion.get(i).getLatitud() + "','" + //latitud
                            puntosPresion.get(i).getLongitud() + "','" + //longitud
                            puntosPresion.get(i).getPresion() + "','" + //presion
                            puntosPresion.get(i).getTipoPresion().getId() + "','" + //tipo presion
                            puntosPresion.get(i).getTipoPunto().getId() + "');"; //tipo punto
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL PUNTO
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(puntosPresion.get(i), a);
                    check++;
                }
                conn.close();
                if (check == puntosPresion.size()) {
                    return "EXITO";
                } else {
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
                Util.mostrarMensaje(a, "Se enviaron los puntos de forma exitosa");
                /*//////////////////////////////////////////////////////////////////////////////////
                 *                      CONCATENO CON LA SIGUIENTE ASYNCTASK
                 */////////////////////////////////////////////////////////////////////////////////*/
                HistorialPuntosControlador historialPuntosControlador = new HistorialPuntosControlador();
                historialPuntosControlador.sincronizarDeSqliteToMysql(a);
            } else {
                Util.mostrarMensaje(a, "Error en el checkHistorialToMysql");
            }
        }
    }

    public int sincronizarDeSqliteToMysql(Activity a) {
        try {
            syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, "Eror SyncSqliteToMysql HPC" + e.toString());
            return Util.ERROR;
        }
    }

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        private ProgressDialog pDialog;
        Activity a;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Trayendo puntos...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public SyncMysqlToSqlite(Activity a) {
            this.a = a;
            check = 0;
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
                            "id_tipo_punto)" +
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
                            rs.getInt(10) + "');"; // id_tipo_punto
                    db.execSQL(sql);
                }
                db.close();
                check++;
                rs.close();
                ps.close();
                conn.close();
                if (check == 1) {
                    return "EXITO";
                } else {
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
                Util.mostrarMensaje(a, "Se copiaron puntos de forma exitosa");
            } else {
                Util.mostrarMensaje(a, "Error en el checkPuntoPresion");
            }
        }
    }

    public int sincronizarDeMysqlToSqlite(Activity a) {
        try {
            syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, "Eror SyncMysqlToSqlite PPC" + e.toString());
            return Util.ERROR;
        }
    }

    public ArrayList<PuntoPresion> extraerTodos(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion", null);
        while (c.moveToNext()) {
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
            puntosPresion.add(puntoPresion);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public PuntoPresion extraerPorId(Activity a, int id) {
        PuntoPresion puntoPresion = new PuntoPresion();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion where id =" + id, null);
        while (c.moveToNext()) {
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
        }
        c.close();
        db.close();
        return puntoPresion;
    }

    public int insertar(PuntoPresion puntoPresion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "INSERT INTO `puntos_presion`" +
                    "(`circuito`," +
                    "`barrio`," +
                    "`calle1`," +
                    "`calle2`," +
                    "`latitud`," +
                    "`longitud`," +
                    "`pendiente`," +
                    "`presion`," +
                    "`id_tipo_presion`," +
                    "`id_tipo_punto`)" +
                    "VALUES" +
                    "('" + puntoPresion.getCircuito() + "','" + // circuito
                    puntoPresion.getBarrio() + "','" + // barrio
                    puntoPresion.getCalle1() + "','" + // calle1
                    puntoPresion.getCalle2() + "','" + // calle2
                    puntoPresion.getLatitud() + "','" + // latitud
                    puntoPresion.getLongitud() + "','" + // longitud
                    "1','" + // pendiente
                    puntoPresion.getPresion() + "','" + // presion
                    puntoPresion.getTipoPresion().getId() + "','" + // tipo_presion
                    puntoPresion.getTipoPunto().getId() + "');"; // tipo_punto
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, "Error insertar PPC " + e.toString());
            return Util.ERROR;
        }
    }

    public ArrayList<PuntoPresion> extraerTodosPendientes(Activity a) {
        puntosPresion = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM puntos_presion " +
                "WHERE pendiente = 1 ", null);
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
            puntosPresion.add(pp);
        }
        c.close();
        db.close();
        return puntosPresion;
    }

    public int actualizarPendiente(PuntoPresion puntoPresion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE puntos_presion " +
                    "SET pendiente = 0 " +
                    "WHERE id=" + puntoPresion.getId();
            db.execSQL(sql);
            db.close();
            return Util.EXITOSO;
        } catch (Exception e) {
            Util.mostrarMensaje(a, "Error actualizarPendiente PPC " + e.toString());
            return Util.ERROR;
        }
    }
}
