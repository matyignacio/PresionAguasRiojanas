package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.controlador.BaseHelper;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class PhotoActivity extends AppCompatActivity {

    ImageView ivFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ivFoto = findViewById(R.id.ivFoto);
        extraerImagen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        abrirActivity(this, InspeccionActivity.class);
    }

    public void extraerImagen() {
        try {
            SQLiteDatabase db = BaseHelper.getInstance(this).getReadableDatabase();
            Cursor c = db.rawQuery("SELECT id, nombre" +
                    " FROM fotos ", null);
            while (c.moveToNext()) {
                byte[] bytes = c.getBlob(1);
                mostrarMensaje(this, " " + c.getBlob(0));
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                ivFoto.setImageBitmap(bmp);
            }
            c.close();
            db.close();
        } catch (Exception e) {
            mostrarMensaje(this, e.toString());
        }
    }
}
