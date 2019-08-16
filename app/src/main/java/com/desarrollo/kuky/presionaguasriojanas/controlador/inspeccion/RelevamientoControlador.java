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
import com.desarrollo.kuky.presionaguasriojanas.controlador.UsuarioControlador;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Relevamiento;
import com.desarrollo.kuky.presionaguasriojanas.ui.LoginActivity;
import com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.InspeccionActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.BANDERA_BAJA;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.INSERTAR_PUNTO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.PRIMER_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.SEGUNDO_INICIO_MODULO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensajeLog;

public class RelevamientoControlador {
    private ProgressDialog pDialog;
    private ArrayList<Relevamiento> relevamientos;

    @SuppressLint("StaticFieldLeak")
    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<Relevamiento> relevamientos;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("3/" +
                    "11 - Enviando Relevamientos...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            relevamientos = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            relevamientos = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection();
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < relevamientos.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    int conexionVisible = relevamientos.get(i).isConexionVisible() ? 1 : 0;
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO relevamiento" +
                            "(id," +
                            "id_usuario," +
                            "barrio," +
                            "tipo_inmueble," +
                            "rubro," +
                            "conexion_visible," +
                            "medidor_luz," +
                            "medidor_agua," +
                            "latitud," +
                            "longitud," +
                            "latitud_usuario," +
                            "longitud_usuario," +
                            "observaciones," +
                            "foto," +
                            "fecha)" +
                            "VALUES " +
                            "('" + relevamientos.get(i).getId() + "', " +
                            "'" + relevamientos.get(i).getIdUsuario() + "', " +
                            "'" + relevamientos.get(i).getBarrio() + "', " +
                            "'" + relevamientos.get(i).getTipoInmueble() + "', " +
                            "'" + relevamientos.get(i).getRubro() + "', " +
                            "'" + conexionVisible + "', " +
                            "'" + relevamientos.get(i).getMedidorLuz() + "', " +
                            "'" + relevamientos.get(i).getMedidorAgua() + "', " +
                            "'" + relevamientos.get(i).getLatitud() + "', " +
                            "'" + relevamientos.get(i).getLongitud() + "', " +
                            "'" + relevamientos.get(i).getLatitudUsuario() + "', " +
                            "'" + relevamientos.get(i).getLongitudUsuario() + "', " +
                            "'" + relevamientos.get(i).getObservaciones() + "', " +
                            "?, " +
                            "'" + relevamientos.get(i).getFecha() + "')";
                    ps = conn.prepareStatement(consultaSql);
                    ps.setBytes(1, relevamientos.get(i).getFoto());
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL inspeccion
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(relevamientos.get(i), a);
                    check++;
                }
                if (check == relevamientos.size()) {
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
                mostrarMensaje(a, "Error en el checkRelevamientoToMysql");
            }
        }
    }

    void sincronizarDeSqliteToMysql(Activity a) {
        try {
            SyncSqliteToMysql syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncSqliteToMysql RC" + e.toString());
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
                    "11 - Recibiendo relevamientos...");
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
                String consultaSql = "SELECT * FROM relevamiento ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM relevamiento");
                while (rs.next()) {
                    ContentValues values = new ContentValues();
                    values.put("id", rs.getInt(1));
                    values.put("id_usuario", rs.getString(2));
                    values.put("barrio", rs.getString(3));
                    values.put("tipo_inmueble", rs.getString(4));
                    values.put("rubro", rs.getString(5));
                    // atendeme ese IF ELSE :)
                    values.put("conexion_visible", rs.getInt(6) == 1 ? 1 : 0);
                    values.put("medidor_luz", rs.getInt(7));
                    values.put("medidor_agua", rs.getInt(8));
                    values.put("latitud", rs.getDouble(9));
                    values.put("longitud", rs.getDouble(10));
                    values.put("latitud_usuario", rs.getDouble(11));
                    values.put("longitud_usuario", rs.getDouble(12));
                    values.put("observaciones", rs.getString(13));
                    values.put("fecha", String.valueOf(rs.getTimestamp(15)));
                    values.put("pendiente", 0);
                    db.insert("relevamiento", "foto", values);
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
                mostrarMensaje(a, "Error en el checkRelevamientos");
            }
        }
    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncMysqlToSqlite RC" + e.toString());
        }
    }

    private void actualizarPendiente(Relevamiento relevamiento, Activity a) {
        String[] whereArgs = {String.valueOf(relevamiento.getId()), relevamiento.getIdUsuario()};
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("pendiente", 0);
            db.update("relevamiento", values,
                    "id = ? AND id_usuario = ?",
                    whereArgs);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente RC " + e.toString());
        }
    }

    private ArrayList<Relevamiento> extraerTodosPendientes(Activity a) {
        relevamientos = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM relevamiento " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            Relevamiento relevamiento = new Relevamiento();
            relevamiento.setId(c.getInt(0));
            relevamiento.setIdUsuario(c.getString(1));
            relevamiento.setBarrio(c.getString(2));
            relevamiento.setTipoInmueble(c.getString(3));
            relevamiento.setRubro(c.getString(4));
            if (c.getInt(5) == 0) {
                relevamiento.setConexionVisible(false);
            } else {
                relevamiento.setConexionVisible(true);
            }
            relevamiento.setMedidorLuz(c.getInt(6));
            relevamiento.setMedidorAgua(c.getInt(7));
            relevamiento.setLatitud(c.getDouble(8));
            relevamiento.setLongitud(c.getDouble(9));
            relevamiento.setLatitudUsuario(c.getDouble(10));
            relevamiento.setLongitudUsuario(c.getDouble(11));
            relevamiento.setObservaciones(c.getString(12));
            relevamiento.setFoto(c.getBlob(13));
            relevamiento.setFecha(Timestamp.valueOf(c.getString(14)));
            relevamientos.add(relevamiento);
        }
        c.close();
        db.close();
        return relevamientos;
    }

    public int insertar(Relevamiento relevamiento, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", relevamiento.getId());
            values.put("id_usuario", relevamiento.getIdUsuario());
            values.put("barrio", relevamiento.getBarrio());
            values.put("tipo_inmueble", relevamiento.getTipoInmueble());
            values.put("rubro", relevamiento.getRubro());
            // atendeme ese IF ELSE :)
            values.put("conexion_visible", relevamiento.isConexionVisible() ? 1 : 0);
            values.put("medidor_luz", relevamiento.getMedidorLuz());
            values.put("medidor_agua", relevamiento.getMedidorAgua());
            values.put("latitud", relevamiento.getLatitud());
            values.put("longitud", relevamiento.getLongitud());
            values.put("latitud_usuario", relevamiento.getLatitudUsuario());
            values.put("longitud_usuario", relevamiento.getLongitudUsuario());
            values.put("observaciones", relevamiento.getObservaciones());
            values.put("foto", relevamiento.getFoto());
            values.put("pendiente", INSERTAR_PUNTO);
            if (db.insert("relevamiento", null, values) > 0) {
                db.close();
                return EXITOSO;
            }
            db.close();
            return ERROR;
        } catch (Exception e) {
            mostrarMensaje(a, "Error insertar RC " + e.toString());
            return ERROR;
        }
    }

    public int obtenerSiguienteId(Activity a) {
        int id = 1;
        try {
            SQLiteDatabase db3 = BaseHelper.getInstance(a).getReadableDatabase();
            String sql = "SELECT id FROM relevamiento ORDER BY id DESC LIMIT 1";
            Cursor c3 = db3.rawQuery(sql, null);
            while (c3.moveToNext()) {
                id = c3.getInt(0) + 1;
            }
            return id;
        } catch (Exception e) {
            mostrarMensaje(a, "Error obtenerSiguienteId RC " + e.toString());
            return ERROR;
        }
    }
}
