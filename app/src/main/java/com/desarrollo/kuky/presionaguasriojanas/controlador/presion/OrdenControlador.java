package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.Orden;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.PuntoPresion;
import com.desarrollo.kuky.presionaguasriojanas.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_ALTA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.TOTAL_ASYNCTASKS;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class OrdenControlador {

    private ProgressDialog pDialog;

    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage((TOTAL_ASYNCTASKS - 2) + "/" + TOTAL_ASYNCTASKS +
                    " - Recibiendo orden...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncMysqlToSqlite(Activity a) {
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
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT * FROM orden ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM orden");
                while (rs.next()) {
                    String sql = "INSERT INTO `orden` " +
                            " VALUES " +
                            "('" + rs.getInt(1) + "','" + // id_pp_actual
                            rs.getInt(2) + "','" + // id_pp_actual
                            rs.getString(3) + "','" + // id_usuario_actual
                            rs.getInt(4) + "','" + // id_pp_siguiente
                            rs.getString(5) + "','" + // id_pp_siguiente
                            rs.getInt(6) + "');"; // id_usuario_siguiente
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
                //mostrarMensaje(a, "4/6 - Se copio el orden con exito");
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                puntoPresionControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkOrden");
            }
        }

    }

    void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncMysqlToSqlite OC" + e.toString());
        }
    }

    public Orden extraerActivo(Activity a) {
        Orden orden = new Orden();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM orden WHERE activo = 1", null);
        while (c.moveToNext()) {
            orden.setId(c.getInt(0));
            /**
             * DEFINIMOS LOS OBJETOS QUE VAMOS A USAR
             */
            PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
            PuntoPresion ppActual;
            PuntoPresion ppSiguiente;
            /**
             * LOS VAMOS CARGANDO CON LOS DATOS DE LA BASE
             */
            ppActual = puntoPresionControlador.extraerPorIdYUsuario(a,
                    c.getInt(1),
                    c.getString(2));
            ppSiguiente = puntoPresionControlador.extraerPorIdYUsuario(a,
                    c.getInt(3),
                    c.getString(4));
            /**
             * LOS SETEAMOS
             */
            orden.setPpActual(ppActual);
            orden.setPpSiguiente(ppSiguiente);
            if (c.getInt(5) == BANDERA_ALTA) {
                orden.setActivo(true);
            } else {
                orden.setActivo(false);
            }
        }
        c.close();
        db.close();
        return orden;
    }

    void editarActivo(Activity a, int id_punto, String id_usuario, int bandera) {
        try {
            SQLiteDatabase bh = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE orden SET activo = " + bandera +
                    " WHERE id_pp_actual =  " + id_punto +
                    " AND id_usuario_pp_actual like '" + id_usuario + "'";
            bh.execSQL(sql);
            bh.close();
        } catch (Exception e) {
            Util.mostrarMensaje(a, e.toString());
        }
    }

    Orden existePunto(Activity a, PuntoPresion puntoPresion) {
        Orden orden = new Orden();
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM orden " +
                    " WHERE id_pp_actual =  " + puntoPresion.getId() +
                    " AND id_usuario_pp_actual like '" + puntoPresion.getUsuario().getId() + "'", null);
            while (c.moveToNext()) {
                orden.setId(c.getInt(0));
                /**
                 * DEFINIMOS LOS OBJETOS QUE VAMOS A USAR
                 */
                PuntoPresionControlador puntoPresionControlador = new PuntoPresionControlador();
                PuntoPresion ppActual;
                PuntoPresion ppSiguiente;
                /**
                 * LOS VAMOS CARGANDO CON LOS DATOS DE LA BASE
                 */
                ppActual = puntoPresionControlador.extraerPorIdYUsuario(a,
                        c.getInt(1),
                        c.getString(2));
                ppSiguiente = puntoPresionControlador.extraerPorIdYUsuario(a,
                        c.getInt(3),
                        c.getString(4));
                /**
                 * LOS SETEAMOS
                 */
                orden.setPpActual(ppActual);
                orden.setPpSiguiente(ppSiguiente);
                if (c.getInt(5) == BANDERA_ALTA) {
                    orden.setActivo(true);
                } else {
                    orden.setActivo(false);
                }
            }
            c.close();
            db.close();
            return orden;
        } catch (Exception e) {
            orden.setId(0);
            Util.mostrarMensaje(a, e.toString());
            return orden;
        }
    }
}
