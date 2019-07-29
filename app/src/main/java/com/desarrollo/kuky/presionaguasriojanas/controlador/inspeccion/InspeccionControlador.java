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
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Cliente;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.DestinoInmueble;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.Inspeccion;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.TipoInmueble;
import com.desarrollo.kuky.presionaguasriojanas.objeto.inspeccion.TipoServicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.ERROR;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.EXITOSO;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class InspeccionControlador {
    private ProgressDialog pDialog;
    private ArrayList<Inspeccion> inspecciones;

    @SuppressLint("StaticFieldLeak")
    private class SyncSqliteToMysql extends AsyncTask<String, Float, String> {

        Activity a;
        private Integer check;
        private ArrayList<Inspeccion> inspecciones;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(a);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setTitle("SINCRONIZANDO");
            pDialog.setMessage("2/" +
                    "9 - Enviando Inspecciones...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        SyncSqliteToMysql(Activity a) {
            this.a = a;
            check = ERROR;
            inspecciones = new ArrayList<>();
        }

        @Override
        protected String doInBackground(String... strings) {
            /**
             IMPLEMENTO TRANSACCIONES CON COMMIT Y ROLLBACK EN LAS TAREAS ASYNCRONAS
             DESDE EL TELEFONO HACIA EL SERVER
             */
            inspecciones = extraerTodosPendientes(a);
            Connection conn;
            conn = Conexion.GetConnection();
            try {
                conn.setAutoCommit(false);
                String consultaSql;
                for (int i = 0; i < inspecciones.size(); i++) {
                /*//////////////////////////////////////////////////////////////////////////////////
                                            INSERTAMOS
                //////////////////////////////////////////////////////////////////////////////////*/
                    int servicioCloacal = inspecciones.get(i).isServicioCloacal() ? 1 : 0;
                    PreparedStatement ps;
                    consultaSql = "INSERT INTO inspeccion " +
                            "(id, id_usuario, id_cliente, id_usuario_cliente, id_tipo_inmueble, " +
                            "id_destino_inmueble, id_tipo_servicio, servicio_cloacal, coeficiente_zonal, " +
                            "latitiud, longitud, latitud_usuario, longitud_usuario, observaciones) " +
                            " VALUES " +
                            "('" + inspecciones.get(i).getId() + "', " +
                            "'" + inspecciones.get(i).getId_usuario() + "', " +
                            "'" + inspecciones.get(i).getCliente().getId() + "', " +
                            "'" + inspecciones.get(i).getCliente().getId_usuario() + "', " +
                            "'" + inspecciones.get(i).getTipoInmueble().getId() + "', " +
                            "'" + inspecciones.get(i).getDestinoInmueble().getId() + "', " +
                            "'" + inspecciones.get(i).getTipoServicio().getId() + "', " +
                            "'" + servicioCloacal + "', " +
                            "'" + inspecciones.get(i).getCoeficienteZonal() + "', " +
                            "'" + inspecciones.get(i).getLatitud() + "', " +
                            "'" + inspecciones.get(i).getLongitud() + "', " +
                            "'" + inspecciones.get(i).getLatitudUsuario() + "', " +
                            "'" + inspecciones.get(i).getLongitudUsuario() + "', " +
                            "'" + inspecciones.get(i).getObservaciones() + "');";
                    ps = conn.prepareStatement(consultaSql);
                    ps.execute();
                    conn.commit();
                    ps.close();
                    /*//////////////////////////////////////////////////////////////////////////////////
                                            BAJAMOS EL PENDIENTE DEL inspeccion
                    //////////////////////////////////////////////////////////////////////////////////*/
                    actualizarPendiente(inspecciones.get(i), a);
                    check++;
                }
                if (check == inspecciones.size()) {
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
                DatosRelevadosControlador datosRelevadosControlador = new DatosRelevadosControlador();
                datosRelevadosControlador.sincronizarDeSqliteToMysql(a);
            } else {
                mostrarMensaje(a, "Error en el checkInspeccionesToMysql");
            }
        }
    }

    void sincronizarDeSqliteToMysql(Activity a) {
        try {
            SyncSqliteToMysql syncSqliteToMysql = new SyncSqliteToMysql(a);
            syncSqliteToMysql.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Error SyncSqliteToMysql IC" + e.toString());
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
            pDialog.setMessage("8/" +
                    "9 - Recibiendo inspecciones...");
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
                String consultaSql = "SELECT * FROM inspeccion ";
                ps = conn.prepareStatement(consultaSql);
                ps.execute();
                rs = ps.getResultSet();
                SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
                /* LIMPIAMOS LA TABLA */
                db.execSQL("DELETE FROM inspeccion");
                while (rs.next()) {
                    String sql = "INSERT INTO inspeccion" +
                            " VALUES" +
                            " ('" + rs.getInt(1) + "','" + // id
                            rs.getString(2) + "','" + //id_usuario
                            rs.getInt(3) + "','" + //id_cliente
                            rs.getString(4) + "','" + //id_usuario_cliente
                            rs.getInt(5) + "','" + //id_tipo_inmueble
                            rs.getInt(6) + "','" + //id_destino_inmueble
                            rs.getInt(7) + "','" + //id_tipo_servicio
                            rs.getInt(8) + "','" + //servicio_cloacal
                            rs.getFloat(9) + "','" + //coeficiente_zonal
                            rs.getDouble(10) + "','" + //latitud
                            rs.getDouble(11) + "','" + //longitud
                            rs.getDouble(12) + "','" + //latitud_usuario
                            rs.getDouble(13) + "','" + //longitud_usuario
                            rs.getString(14) + "','" + //observaciones
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
                DatosRelevadosControlador datosRelevadosControlador = new DatosRelevadosControlador();
                datosRelevadosControlador.sincronizarDeMysqlToSqlite(a);
            } else {
                mostrarMensaje(a, "Error en el checkinspeccion");
            }
        }

    }

    public void sincronizarDeMysqlToSqlite(Activity a) {
        try {
            SyncMysqlToSqlite syncMysqlToSqlite = new SyncMysqlToSqlite(a);
            syncMysqlToSqlite.execute();
        } catch (Exception e) {
            mostrarMensaje(a, "Eror SyncMysqlToSqlite CC" + e.toString());
        }
    }

    private void actualizarPendiente(Inspeccion inspeccion, Activity a) {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(a).getWritableDatabase();
            String sql = "UPDATE inspeccion" +
                    " SET pendiente = 0" +
                    " WHERE id=" + inspeccion.getId() +
                    " AND id_usuario LIKE '" + inspeccion.getId_usuario() + "'";
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            mostrarMensaje(a, "Error actualizarPendiente IC " + e.toString());
        }
    }

    private ArrayList<Inspeccion> extraerTodosPendientes(Activity a) {
        inspecciones = new ArrayList<>();
        SQLiteDatabase db = BaseHelper.getInstance(a).getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM inspeccion " +
                "WHERE pendiente = 1 " +
                "ORDER BY id ASC", null);
        while (c.moveToNext()) {
            Inspeccion inspeccion = new Inspeccion();
            inspeccion.setId(c.getInt(0));
            inspeccion.setId_usuario(c.getString(1));
            inspeccion.setCliente(new Cliente(c.getInt(2)));
            inspeccion.setTipoInmueble(new TipoInmueble(c.getInt(3)));
            inspeccion.setDestinoInmueble(new DestinoInmueble(c.getInt(4)));
            inspeccion.setTipoServicio(new TipoServicio(c.getInt(5)));
            if (c.getInt(6) == 0) {
                inspeccion.setServicioCloacal(false);
            } else {
                inspeccion.setServicioCloacal(true);
            }
            inspeccion.setCoeficienteZonal(c.getFloat(7));
            inspeccion.setLatitud(c.getDouble(8));
            inspeccion.setLongitud(c.getDouble(9));
            inspeccion.setLatitudUsuario(c.getDouble(10));
            inspeccion.setLongitudUsuario(c.getDouble(11));
            inspeccion.setObservaciones(c.getString(12));
            inspecciones.add(inspeccion);
        }
        c.close();
        db.close();
        return inspecciones;
    }
}
