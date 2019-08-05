package com.desarrollo.kuky.presionaguasriojanas.ui.inspeccion.nuevorelevamientofragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.desarrollo.kuky.presionaguasriojanas.R;

import java.io.File;

public class FormFoto extends Fragment {
    //CONSTANTES PARA LA CAMARA
    public static final String CARPETA_RAIZ = "misImagenesPrueba/";
    public static final String RUTA_IMAGEN = CARPETA_RAIZ + "misFotos";
    public static final int TAKE_PHOTO = 20;
    private String path, ruta = "";
    private File fileImagen;
    private File image;
    ImageButton ivFoto;

    public FormFoto() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_form_foto_relevamiento, container, false);
        //Nuevos parametros para el view del fragmento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        //Nueva Regla: EL fragmento estara debajo del boton add_fragment
        params.addRule(RelativeLayout.ABOVE, R.id.bSiguienteFragmento);
        //Margenes: top:15dp
        params.setMargins(0, 15, 0, 15);
        //Setear los parametros al view
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ivFoto = getActivity().findViewById(R.id.ivFoto);
        ivFoto.setOnClickListener(v -> {
            tomarFotografia();
        });
    }

    private void tomarFotografia() {
        fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();
        String nombreImagen = "";
        if (!isCreada) {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada) {
            nombreImagen = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory() +
                File.separator + RUTA_IMAGEN + File.separator + nombreImagen;

        image = new File(path);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = getActivity().getApplicationContext().getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(getActivity(), authorities, image);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            System.out.println("IMAGE URI" + imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));

        }
        startActivityForResult(intent, TAKE_PHOTO);

    }
}
