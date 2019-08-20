package com.desarrollo.kuky.presionaguasriojanas.controlador.inspeccion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;
import com.desarrollo.kuky.presionaguasriojanas.controlador.Conexion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.RelevamientoMedidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ASYNCTASK_INSPECCION;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;

public class RelevamientoMedidorControlador {
    private ProgressDialog pDialog;
    private ArrayList<RelevamientoMedidor> relevamientoMedidores;

    @SuppressLint("StaticFieldLeak")
    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<RelevamientoMedidor> relevamientoMedidores;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("4/" +
                    +ASYNCTASK_INSPECCION + " - Enviando Relevamiento Medidores...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            relevamientoMedidores = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            relevamientoMedidores = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection();
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < relevamientoMedidores.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO relevamiento_medidores" +
                            "(id," +
                            "id_usuario," +
                            "numero," +
                            "id_relevamiento," +
                            "id_usuario_relevamiento)" +
                            "VALUES " +
                            "('" + relevamientoMedidores.get(i).getId() + "', " +
                            "'" + relevamientoMedidores.get(i).getIdUsuario() + "', " +
                            "'" + relevamientoMedidores.get(i).getNumero() + "', " +
                            "'" + relevamientoMedidores.get(i).getRelevamiento().getId() + "', " +
                            "'" + relevamientoMedidores.get(i).getRelevamiento().getIdUsuario() + "')";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL inspeccion
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(relevamientoMedidores.get(i), a);
                    check++;
                }
                if (check == relevamientoMedidores.size()) {
                    return "EXITO";
                } else {
                    return "ERROR";
                }
            } catch (SQLException e) {
                try {
                    mostrarMensajeLog(a, e.toString());
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
                DatosRelevadosControlador datosRelevadosControlador = new DatosRelevadosControlador();
                datosRelevadosControlador.sincronizarDeSqliteToMysql(a);
                // VACIAMOS LA TABLA ?????
                // SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                // db.delete("relevamiento", null, null);
            } else {
                mostrarMensaje(a, "Error en el checkRelevamientoMedidoresToMysql");
            }
        }
    }

    void sincronizarDeSqliteToMysql(Activity a) {
        try {
            SyncSqliteToMysql syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncSqliteToMysql RMC" + e.toString());
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
            pDialog.setMessage("11/" +
                    +ASYNCTASK_INSPECCION + " - Recibiendo Relevamiento Medidores...");
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
                String consultaSql = "SELECT * FROM relevamiento_medidores ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM relevamiento_medidores");
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("id", rs.getInt(1));
                    values.put("id_usuario", rs.getString(2));
                    values.put("numero", rs.getInt(3));
                    values.put("id_relevamiento", rs.getInt(4));
                    values.put("id_usuario_relevamiento", rs.getString(5));
                    values.put("pendiente", 0);
                    db.insert("relevamiento_medidores", null, values);
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
                DatosRelevadosControlador datosRelevadosControlador = new DatosRelevadosControlador();
                datosRelevadosControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkRelevamientoMedidores");
            }
        }
    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncMysqlToSqlite RMC" + e.toString());
        }
    }

    private void actualizarPendiente(RelevamientoMedidor relevamientoMedidor, Activity a) {
        String[] whereArgs = {String.valueOf(relevamientoMedidor.getId()), relevamientoMedidor.getIdUsuario()};
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("pendiente", 0);
            db.update("relevamiento_medidores", values,
                    "id = ? AND id_usuario = ?",
                    whereArgs);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente RMC " + e.toString());
        }
    }

    private ArrayList<RelevamientoMedidor> extraerTodosPendientes(Activity a) {
        relevamientoMedidores = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM relevamiento_medidores " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            RelevamientoMedidor relevamiento = new RelevamientoMedidor();
            relevamiento.setId(c.getInt(0));
            relevamiento.setIdUsuario(c.getString(1));
            relevamientoMedidores.add(relevamiento);
        }
        c.close();
        db.close();
        return relevamientoMedidores;
    }

    public int insertar(RelevamientoMedidor relevamiento, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", relevamiento.getId());
            values.put("id_usuario", relevamiento.getIdUsuario());
            values.put("numero", relevamiento.getNumero());
            values.put("id_relevamiento", relevamiento.getRelevamiento().getId());
            values.put("id_usuario_relevamiento", relevamiento.getRelevamiento().getIdUsuario());
            values.put("pendiente", INSERTAR_PUNTO);
            if (db.insert("relevamiento_medidores", null, values) > 0) {
                db.close();
                return EXITOSO;
            }
            db.close();
            return ERROR;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar RMC " + e.toString());
            return ERROR;
        }
    }

    public int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM relevamiento_medidores ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId RMC " + e.toString());
            return ERROR;
        }
    }
}
