package com.desarrollo.kuky.presionaguasriojanas.ui;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.desarrollo.kuky.presionaguasriojanas.R;

import static com.desarrollo.kuky.presionaguasriojanas.util.Util.abrirActivity;
import static com.desarrollo.kuky.presionaguasriojanas.util.Util.mostrarMensaje;

public class PruebaFused extends AppCompatActivity implements LocationListener {
    Button button;
    boolean gps_enabled;
    double speed = 0;
    double latitude = 0;
    double longitude = 0;
    String speed1;
    String latitude1;
    String longitude1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_fused);
        button = findViewById(R.id.button);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0f, this);
        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onBackPressed() {
        abrirActivity(this, InicioActivity.class);
    }

    @Override
    public void onLocationChanged(Location location) {
        speed = location.getSpeed();
        speed1 = Double.toString(speed);

        latitude = location.getLatitude();
        latitude1 = Double.toString(latitude);

        longitude = location.getLongitude();
        longitude1 = Double.toString(longitude);
        button.setText("Latitud: " + latitude1);
        mostrarMensaje(this, latitude1 + longitude1);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
