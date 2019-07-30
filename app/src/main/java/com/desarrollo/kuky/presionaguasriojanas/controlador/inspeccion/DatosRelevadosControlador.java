package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.DatosRelevados;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.InspeccionActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SEGUNDO_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class DatosRelevadosControlador {
    private ProgressDialog pDialog;
    private ArrayList<DatosRelevados> datosRelevados;

    @SuppressLint("StaticFieldLeak")
    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<DatosRelevados> datosRelevados;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("3/" +
                    "9 - Enviando Datos relevados...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            datosRelevados = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            datosRelevados = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection();
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < datosRelevados.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    int estado = datosRelevados.get(i).isEstado() ? 1 : 0;
                    int medida = datosRelevados.get(i).isMedida() ? 1 : 0;
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO datos_relevados" +
                            "(id, id_usuario, unidad, estado, medida, med_agua, " +
                            "med_luz, nis, id_inpseccion, id_usuario_inspeccion)" +
                            " VALUES " +
                            "('" + datosRelevados.get(i).getId() + "', " +
                            "'" + datosRelevados.get(i).getIdUsuario() + "', " +
                            "'" + datosRelevados.get(i).getUnidad() + "', " +
                            "'" + estado + "', " +
                            "'" + medida + "', " +
                            "'" + datosRelevados.get(i).getMedidorAgua() + "', " +
                            "'" + datosRelevados.get(i).getMedidorLuz() + "', " +
                            "'" + datosRelevados.get(i).getNis() + "', " +
                            "'" + datosRelevados.get(i).getInspeccion().getId() + "', " +
                            "'" + datosRelevados.get(i).getInspeccion().getIdUsuario() + "');";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL datos_relevados
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(datosRelevados.get(i), a);
                    check++;
                }
                if (check == datosRelevados.size()) {
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
                TipoInmuebleControlador tipoInmuebleControlador = new TipoInmuebleControlador();
                tipoInmuebleControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkDatosRelevadosToMysql");
            }
        }
    }

    public void sincronizarDeSqliteToMysql(Activity a) {
        try {
            SyncSqliteToMysql syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncSqliteToMysql DRC" + e.toString());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SyncMysqlToSqlite extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("9/" +
                    "9 - Recibiendo datos relevados...");
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
                String consultaSql = "SELECT * FROM datos_relevados ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM datos_relevados");
                while (rs.next()) {
                    String sql = "INSERT INTO datos_relevados" +
                            " VALUES" +
                            " ('" + rs.getInt(1) + "','" + // id
                            rs.getString(2) + "','" + //id_usuario
                            rs.getInt(3) + "','" + //unidad
                            rs.getInt(4) + "','" + //estado
                            rs.getInt(5) + "','" + //medida
                            rs.getInt(6) + "','" + //med_agua
                            rs.getInt(7) + "','" + //med_luz
                            rs.getInt(8) + "','" + //nis
                            rs.getInt(9) + "','" + //id_inspeccion
                            rs.getString(10) + "','" + //id_usuario_inspeccion
                            "0');"; // pendiente
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
                mostrarMensaje(a, "Se sincronizo con exito!");
                UsuarioControlador usuarioControlador = new UsuarioControlador();
                if (LoginActivity.usuario.getBanderaModuloInspeccion() == PRIMER_INICIO_MODULO) {
                    usuarioControlador.editarBanderaModuloInspeccion(a, SEGUNDO_INICIO_MODULO);
                }
                usuarioControlador.editarBanderaSyncModuloInspeccion(a, BANDERA_BAJA);
                abrirActivity(a, InspeccionActivity.class);
            } else {
                mostrarMensaje(a, "Error en el checkDatosRelevados");
            }
        }
    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncMysqlToSqlite DRC" + e.toString());
        }
    }

    private void actualizarPendiente(DatosRelevados datoRelevado, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE datos_relevados" +
                    " SET pendiente = 0" +
                    " WHERE id=" + datoRelevado.getId() +
                    " AND id_usuario LIKE '" + datoRelevado.getIdUsuario() + "'";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente DRC " + e.toString());
        }
    }

    private ArrayList<DatosRelevados> extraerTodosPendientes(Activity a) {
        datosRelevados = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM datos_relevados " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            DatosRelevados datoRelevado = new DatosRelevados();
            datoRelevado.setId(c.getInt(0));

            datosRelevados.add(datoRelevado);
        }
        c.close();
        db.close();
        return datosRelevados;
    }
}
