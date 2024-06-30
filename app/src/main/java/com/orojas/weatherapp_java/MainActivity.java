package com.orojas.weatherapp_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.orojas.weatherapp_java.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private Spinner spinnerCities;
    private List<String> cities = Arrays.asList("Santiago", "Valparaíso", "La Serena", "Concepción", "Puerto Montt", "Punta Arenas");

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            goLoginOption();
        }
        // Inicialización de Vistas
        spinnerCities = findViewById(R.id.sp_ciudades);

        // Configuración Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(adapter);

        // Registrar el listener en el Spinner
        spinnerCities.setOnItemSelectedListener(this);
    }

    private void goLoginOption() {
        startActivity(new Intent(this, OptionLoginActivity.class));
    }

    // Métodos del listener OnItemSelectedListener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCity = cities.get(position);
        // Evaluamos el valor que ciudad se Seleccionó
        switch (selectedCity) {
            case "Santiago":
                Toast.makeText(this, "Seleccionaste Santiago", Toast.LENGTH_SHORT).show();
                break;
            case "Valparaíso":
                Toast.makeText(this, "Seleccionaste Valparaíso", Toast.LENGTH_SHORT).show();
                break;
            case "La Serena":
                Toast.makeText(this, "Seleccionaste La Serena", Toast.LENGTH_SHORT).show();
                break;
            case "Concepción":
                Toast.makeText(this, "Seleccionaste Concepción", Toast.LENGTH_SHORT).show();
                break;
            case "Puerto Montt":
                Toast.makeText(this, "Seleccionaste Puerto Montt", Toast.LENGTH_SHORT).show();
                break;
            case "Punta Arenas":
                Toast.makeText(this, "Seleccionaste Punta Arenas", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Este método se llama cuando no se selecciona ningún ítem
    }
}
