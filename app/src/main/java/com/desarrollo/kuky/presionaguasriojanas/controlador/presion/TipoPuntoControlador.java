package com.desarrollo.kuky.presionaguasriojanas.controlador.presion;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.presion.TipoPunto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.checkConnection;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.progressBarVisibility;

public class TipoPuntoControlador {

    private class SyncMysqlToSqlite extends AsyncTask<String, Integer, String> {

        Activity a;
        private Integer check;
        private ProgressBar progressBar;
        private TextView tvProgressBar;

        @Override
        protected void onPreExecute() {
            progressBar.setMax(10);
            progressBar.setProgress(0);
            progressBarVisibility(progressBar, tvProgressBar, true);
        }

        SyncMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
            this.a = a;
            check = ERROR;
            this.progressBar = progressBar;
            this.tvProgressBar = tvProgressBar;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection conn;
                PreparedStatement ps;
                ResultSet rs;
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                conn = Conexion.GetConnection();
                String consultaSql = "SELECT * FROM tipo_punto ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM tipo_punto");
                while (rs.next()) {
                    String sql = "INSERT INTO `tipo_punto`" +
                            "VALUES" +
                            "('" + rs.getInt(1) + "','" + // id
                            rs.getString(2) + "');"; // nombre
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
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBarVisibility(progressBar, tvProgressBar, false);
            if (s.equals("EXITO")) {
                //mostrarMensaje(a, "3/6 - Se copio los tipos de puntos con exito");
                OrdenControlador ordenControlador = new OrdenControlador();
                ordenControlador.sincronizarDeMysqlToSqlite(a, progressBar, tvProgressBar);
            } else {
                mostrarMensaje(a, "Error en el checkTipoPunto");
            }
        }
    }

    public void sincronizarDeMysqlToSqlite(Activity a, ProgressBar progressBar, TextView tvProgressBar) {
        checkConnection(a, () -> {
            try {
                SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a, progressBar, tvProgressBar);
                syncMysqlToSqlite.execute();
            } catch (Exception e) {
                mostrarMensaje(a, "Error SyncMysqlToSqlite TPC" + e.toString());
            }
            return null;
        });
    }

    public ArrayList<TipoPunto> extraerTodos(Activity a) {
        ArrayList<TipoPunto> tipoPuntos = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM tipo_punto", null);
        while (c.moveToNext()) {
            TipoPunto tp = new TipoPunto();
            tp.setId(c.getInt(0));
            tp.setNombre(c.getString(1));
            tipoPuntos.add(tp);
        }
        c.close();
        db.close();
        return tipoPuntos;
    }
}
