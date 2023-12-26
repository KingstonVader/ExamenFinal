    package com.example.examenfinal;

    import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.location.Location;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;
    import android.os.CountDownTimer;
    import android.widget.TextView;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.maps.DirectionsApi;
    import com.google.maps.GeoApiContext;
    import com.google.maps.PendingResult;
    import com.google.maps.model.DirectionsResult;
    import com.google.maps.model.TravelMode;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.Locale;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;




    public class RegistroFecha extends AppCompatActivity {

        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private FusedLocationProviderClient fusedLocationClient;
        private String coordenadasIniciales = "";
        Button buttonBackT, buttonOn, buttonOff,buttonInicio,buttonFin;
        private RegistroFechaCronometro registroFechaCronometro;
        TextView textViewCronometro;
        private long tiempoRestante = 0;
        private boolean cronometroCorriendo = false;
        private CountDownTimer countDownTimer;
        private GeoApiContext context;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registro_fecha);

            buttonBackT = findViewById(R.id.buttonBackT);
            buttonOn = findViewById(R.id.buttonInicio);
            buttonOff = findViewById(R.id.buttonFin);
            buttonInicio = findViewById(R.id.buttonInicio);

            buttonFin = findViewById(R.id.buttonFin);

            context = new GeoApiContext.Builder( )
                    .apiKey("AIzaSyDvquKfTHuZRehE9amaNT8q5E6TjbADCTE")
                    .build( );



            buttonFin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detenerCronometro();
                }

                private void detenerCronometro() {

                }
            });

            fusedLocationClient = getFusedLocationProviderClient(this);
            registroFechaCronometro = new RegistroFechaCronometro(new RegistroFechaCronometro.OnCronometroTickListener() {

                @Override
                public void onTick(String tiempoRestante) {
                    actualizarTextoCronometro(tiempoRestante);
                }

                @Override
                public void onFinish() {

                }

                private void actualizarTextoCronometro(String tiempoRestante) {

                }

                private void iniciarCronometro() {
                    if (!cronometroCorriendo) {
                        countDownTimer = new CountDownTimer(tiempoRestante, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tiempoRestante = millisUntilFinished;
                                actualizarTextoCronometro();
                            }

                            @Override
                            public void onFinish() {
                                detenerCronometro();
                            }
                        }.start();

                        cronometroCorriendo = true;
                        buttonInicio.setEnabled(false); // Deshabilitar el botón de inicio mientras el cronómetro está en marcha
                    }
                }

                private void detenerCronometro() {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }

                    cronometroCorriendo = false;
                    tiempoRestante = 0;
                    actualizarTextoCronometro();
                    buttonInicio.setEnabled(true); // Habilitar el botón de inicio después de detener el cronómetro
                }

                private void actualizarTextoCronometro() {
                    int minutos = (int) (tiempoRestante / 1000) / 60;
                    int segundos = (int) (tiempoRestante / 1000) % 60;

                    String tiempoFormato = String.format("%02d:%02d", minutos, segundos);
                    textViewCronometro.setText(tiempoFormato);

                }
            });

            buttonBackT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RegistroFecha.this, Home.class);
                    startActivity(intent);
                }
            });


            buttonOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obtenerUbicacionActual();
                }
            });

            buttonOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarUltimasCoordenadasEnEditText();
                    guardarDatosEnFirebase();
                }
            });

        }
        private void calcularDistanciaDuracion(String inicio, String fin) {
            DirectionsApi.newRequest(context)
                    .mode(TravelMode.DRIVING)
                    .origin(String.valueOf(new LatLng(Double.parseDouble(inicio.split(",")[0]), Double.parseDouble(inicio.split(",")[1]))))
                    .destination(String.valueOf(new LatLng(Double.parseDouble(fin.split(",")[0]), Double.parseDouble(fin.split(",")[1]))))
                    .setCallback(new PendingResult.Callback<DirectionsResult>() {
                        @Override
                        public void onResult(DirectionsResult result) {
                            try {
                                if (result != null && result.routes != null && result.routes.length > 0) {
                                    if (result.routes[0].legs != null && result.routes[0].legs.length > 0) {
                                        // Obtener la distancia y la duración desde el resultado de la ruta
                                        String distancia = result.routes[0].legs[0].distance != null ? result.routes[0].legs[0].distance.humanReadable : "N/A";
                                        String duracion = result.routes[0].legs[0].duration != null ? result.routes[0].legs[0].duration.humanReadable : "N/A";

                                        // Actualizar la interfaz de usuario en el hilo principal
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                actualizarInterfazUsuario(distancia, duracion);
                                            }
                                        });

                                        // Ahora puedes llamar a guardarDatosEnFirebase() ya que tienes las coordenadas finales y otra información relevante.
                                        guardarDatosEnFirebase(distancia, duracion);
                                    } else {
                                        Log.e("Error", "No hay información de piernas en la ruta");
                                    }
                                } else {
                                    Log.e("Error", "No hay rutas disponibles");
                                }
                            } catch (Exception e) {
                                Log.e("Error", "Error al calcular la ruta: " + e.getMessage());
                            }
                        }

                        private void guardarDatosEnFirebase(String distancia, String duracion) {
                        }

                        @Override
                        public void onFailure(Throwable e) {
                            Log.e("Error", "Error al calcular la ruta: " + e.getMessage());
                        }
                    });
        }

        private void actualizarInterfazUsuario(String distancia, String duracion) {
            // Actualizar tus EditText con la distancia y la duración
        }


        private void mostrarUltimasCoordenadasEnEditText() {
            String ultimasCoordenadas = obtenerUltimasCoordenadas();
            Log.d("TAG", "Ultimas coordenadas: " + ultimasCoordenadas);
            EditText editFinRuta = findViewById(R.id.editFinRuta);
            editFinRuta.setText(ultimasCoordenadas);
        }

        private void obtenerUbicacionActual() {
            Log.d("TAG", "obtenerUbicacionActual() llamado");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Obtener coordenadas iniciales
                            coordenadasIniciales = location.getLatitude() + ", " + location.getLongitude();

                            // Obtener la fecha actual
                            String fechaActual = obtenerFechaActual();

                            // Mostrar las coordenadas iniciales y la fecha en los EditText
                            EditText editInicioRuta = findViewById(R.id.editInicioRuta);
                            EditText editFechaRuta = findViewById(R.id.editFechaRuta);

                            editInicioRuta.setText(coordenadasIniciales);
                            editFechaRuta.setText(fechaActual);
                        } else {
                            Toast.makeText(RegistroFecha.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        private String obtenerFechaActual() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            return dateFormat.format(date);
        }

        private void guardarDatosEnFirebase() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference referencia = database.getReference("rutas");

            String nombreRuta = ((EditText) findViewById(R.id.editNameRuta)).getText().toString();
            String fechaRuta = ((EditText) findViewById(R.id.editFechaRuta)).getText().toString();
            String coordenadasFinales = ((EditText) findViewById(R.id.editFinRuta)).getText().toString();

            Ruta nuevaRuta = new Ruta(nombreRuta, fechaRuta, coordenadasIniciales, coordenadasFinales);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            referencia.child(userId).push().setValue(nuevaRuta);
            guardarUltimasCoordenadas(coordenadasIniciales);

            Toast.makeText(RegistroFecha.this, "Se guardaron los datos", Toast.LENGTH_SHORT).show();
        }

        private void guardarUltimasCoordenadas(String coordinates) {
            getPreferences(MODE_PRIVATE).edit().putString("last_coordinates", coordinates).apply();
        }

        private String obtenerUltimasCoordenadas() {
            return getPreferences(MODE_PRIVATE).getString("last_coordinates", "No hay coordenadas almacenadas");
        }

    }