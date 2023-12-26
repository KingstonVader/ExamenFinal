package com.example.examenfinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap googleMap;
    private DatabaseReference databaseReference;
    private Spinner spinnerRoutesHistory;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button btnShowMapTypeDialog;
    private List<LatLng> rutaPoints;
    private Polyline rutaPolyline;
    private double distanciaTotal;
    private BreakIterator textViewdistancia;
    private LatLng inicioRutaLatLng;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        databaseReference = FirebaseDatabase.getInstance().getReference("ubicaciones");

        SupportMapFragment mapa = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapa.getMapAsync(this);

        getLocalizacion();

        btnShowMapTypeDialog = findViewById(R.id.btnShowMapTypeDialog);
        btnShowMapTypeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeDialog();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (!addresses.isEmpty()) {
                        Toast.makeText(Mapa.this, "Ubicacion actual: " + addresses.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(Mapa.this, "Habilita el proveedor de ubicación", Toast.LENGTH_SHORT).show();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
    }

    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void actualizarRuta(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng nuevaPosicion = new LatLng(latitude, longitude);
            rutaPoints.add(nuevaPosicion);
            rutaPolyline.setPoints(rutaPoints);

            if (rutaPoints.size() > 1) {
                LatLng puntoAnterior = rutaPoints.get(rutaPoints.size() - 2);
                double distancia = calcularDistancia(puntoAnterior, nuevaPosicion);
                distanciaTotal += distancia;
                textViewdistancia.setText(String.format("%.2f km", distanciaTotal / 1000));
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(nuevaPosicion));
        }
    }

    private double calcularDistancia(LatLng puntoAnterior, LatLng nuevaPosicion) {
        // Aquí debes implementar la lógica para calcular la distancia entre dos puntos LatLng.
        // Puedes usar la clase Location para esto.
        Location locationAnterior = new Location("");
        locationAnterior.setLatitude(puntoAnterior.latitude);
        locationAnterior.setLongitude(puntoAnterior.longitude);

        Location locationNueva = new Location("");
        locationNueva.setLatitude(nuevaPosicion.latitude);
        locationNueva.setLongitude(nuevaPosicion.longitude);

        return locationAnterior.distanceTo(locationNueva);
    }

    private Location obtenerPosicionActual() {
        // Obtén la última ubicación conocida del proveedor GPS.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng CHILE = new LatLng(-30.603083, -71.203088);
        googleMap.addMarker(new MarkerOptions().position(CHILE).title("Ubicación Clickeada"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(CHILE));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(CHILE)
                .zoom(14)
                .bearing(90)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        googleMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Nuevo Marcador"));
        Toast.makeText(Mapa.this, "Latitud: " + latLng.latitude + ", Longitud: " + latLng.longitude, Toast.LENGTH_SHORT).show();
    }

    private void showMapTypeDialog() {
        final String[] mapTypes = {"Normal", "Satelital", "Híbrido", "Terreno"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Mapa.this);
        builder.setTitle("Seleccionar Tipo de Mapa");
        builder.setItems(mapTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cambiar el tipo de mapa según la opción seleccionada
                switch (which) {
                    case 0:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 3:
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
            }
        });
        builder.show();
    }
}
