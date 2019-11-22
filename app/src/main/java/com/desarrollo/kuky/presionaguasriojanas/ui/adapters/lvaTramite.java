package com.desarrollo.kuky.presionaguasriojanas.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.desarrollo.kuky.presionaguasriojanas.R;
import com.desarrollo.kuky.presionaguasriojanas.objeto.reclamo.Tramite;

import java.util.ArrayList;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.setPrimaryFontBold;

public class lvaTramite extends BaseAdapter {
    // Declare Variables
    private Context context;
    private ArrayList<Tramite> tramites;

    public lvaTramite(Context context, ArrayList<Tramite> tramites) {
        this.context = context;
        this.tramites = tramites;
    }

    @Override
    public int getCount() {
        return tramites.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        // Declare Variables
        TextView tvCliente, tvMotivo, tvDescripcion;

        //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.sli_tramite, parent, false);

        // Locate the TextViews in listview_item.xml
        tvCliente = itemView.findViewById(R.id.tvCliente);
        tvMotivo = itemView.findViewById(R.id.tvMotivo);
        tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        // Capture position and set to the TextViews
        /** SETEAMOS LOS TYPEFACES*/
        setPrimaryFontBold(context, tvCliente);
        setPrimaryFontBold(context, tvMotivo);
        setPrimaryFontBold(context, tvDescripcion);
        /**************************/
        tvCliente.setText(tramites.get(position).getReclamo().getRazonSocial());
        tvMotivo.setText(tramites.get(position).getMotivo().getDescripcion());
        tvDescripcion.setText(tramites.get(position).getDescripcion());

        return itemView;
    }
}
