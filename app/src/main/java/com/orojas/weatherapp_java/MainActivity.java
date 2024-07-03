package com.orojas.weatherapp_java;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Spinner spinnerCities;
    private Button btnVerPronostico;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private List<String> cityList;
    private Map<String, LatLng> cityCoordinates;
    private Retrofit retrofit;

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "e59d1b4dfaca37ea6841b17a5cadad02";

    private WeatherService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherService = retrofit.create(WeatherService.class);

        spinnerCities = findViewById(R.id.spinner_cities);
        btnVerPronostico = findViewById(R.id.btn_ver_pronostico);

        // Inicializar la lista de ciudades principales de Chile
        cityList = new ArrayList<>();
        cityList.add("Santiago");
        cityList.add("Valparaíso");
        cityList.add("Concepción");
        cityList.add("La Serena");
        cityList.add("Antofagasta");

        // Inicializar coordenadas de ciudades
        cityCoordinates = new HashMap<>();
        cityCoordinates.put("Santiago", new LatLng(-33.4489, -70.6693));
        cityCoordinates.put("Valparaíso", new LatLng(-33.0458, -71.6197));
        cityCoordinates.put("Concepción", new LatLng(-36.8270, -73.0503));
        cityCoordinates.put("La Serena", new LatLng(-29.9027, -71.2519));
        cityCoordinates.put("Antofagasta", new LatLng(-23.6500, -70.4000));

        // Crear un ArrayAdapter usando la lista de ciudades y un layout por defecto del spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(adapter);

        // Configurar el listener para el spinner
        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cityList.get(position);
                loadCityMap(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // Configurar el listener para el botón
        btnVerPronostico.setOnClickListener(v -> {
            String selectedCity = spinnerCities.getSelectedItem().toString();
            getWeatherForCity(selectedCity);
        });

        // Obtener el fragmento de Google Maps y notificar cuando el mapa esté listo para usarse
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicializar Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherService = retrofit.create(WeatherService.class);
    }


    private void getWeatherForCity(String city) {
        Call<WeatherResponse> call = weatherService.getCurrentWeather(city, API_KEY, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    float temp = weatherResponse.getMain().getTemp();
                    String description = weatherResponse.getWeather().get(0).getDescription();

                    // Actualiza la UI con los datos del clima
                    String weatherInfo = "Temp: " + temp + "°C\nDescription: " + description;
                    Toast.makeText(MainActivity.this, weatherInfo, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to get weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Verificar y solicitar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            loadCurrentCityMap();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void loadCityMap(String cityName) {
        // Cargar el mapa de la ciudad seleccionada usando las coordenadas
        LatLng cityLatLng = cityCoordinates.get(cityName);
        if (cityLatLng != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(cityLatLng).title(cityName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 12));
        } else {
            Toast.makeText(this, "No se encontraron coordenadas para la ciudad seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCurrentCityMap() {
        // Cargar el mapa de la ubicación actual del dispositivo
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Ubicación actual"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCurrentCityMap();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
