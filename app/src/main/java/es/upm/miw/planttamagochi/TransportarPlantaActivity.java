package es.upm.miw.planttamagochi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import es.upm.miw.planttamagochi.pojo.Example;
import es.upm.miw.planttamagochi.service.openWeather.LoadOpenWeatherTask;

public class TransportarPlantaActivity extends AppCompatActivity implements View.OnClickListener {

    static String LOG_TAG = "Transp";

    private Spinner spinner;

    private String[] ciudades = {"", "Madrid", "Barcelona", "Salamanca", "Sevilla",
            "Manchester", "London" ,"Brasília", "Cairo", "California", "Jhānsi", "Stuttgart"};

    private String[] recomendacion = {"Es un lugar ideal, la planta se mantendrá correctamente",
            "Cuidado!, si lleva la planta tendrá que tener en cuenta la temperatura",
            "Bien, pero si lleva la planta tendrá que tener en cuenta la humedad",
            "¡NO lleve la planta!, o sufrirá daños"};

    LoadOpenWeatherTask openWeatherTask;
    private String ciudad = "";
    Example recursoOpenWeather;

    TextView txtCiudad, txtRecomendacion;
    ImageView imagenRecomendacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transportar_planta);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ciudades);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        txtCiudad = (TextView) findViewById(R.id.txtCiudad);
        txtRecomendacion = (TextView) findViewById(R.id.txtRecomendacion);

        imagenRecomendacion = (ImageView) findViewById(R.id.imagenRecomendacion);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ciudad = spinner.getSelectedItem().toString();
                txtCiudad.setText(ciudad);
                imagenRecomendacion.setImageResource(0);
                txtRecomendacion.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        //Click Listeners
        findViewById(R.id.buttonComprobar).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonComprobar) {
            if (ciudad != "") {
                this.loadOpenWeatherTask();
            } else {
                Toast.makeText(TransportarPlantaActivity.this, "Selecciona antes una ciudad", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    protected void loadOpenWeatherTask() {
        openWeatherTask = new LoadOpenWeatherTask();

        try {
            recursoOpenWeather = openWeatherTask.execute(ciudad).get();

            Double tempCiudad = recursoOpenWeather.getMain().getTemp();
            tempCiudad = this.celsiusToCentigrades(tempCiudad);

            int humidity = recursoOpenWeather.getMain().getHumidity();

            Log.e(LOG_TAG, "Valor temperatura: " + tempCiudad + " | " + "Valor humedad: " + humidity);

            if ((tempCiudad > 18.0) && (tempCiudad < 25.0)) {
                if ((humidity > 80) && (humidity < 90)) {
                    imagenRecomendacion.setImageResource(R.drawable.tamagochi_happy);
                    txtRecomendacion.setText(recomendacion[0]);
                } else {
                    imagenRecomendacion.setImageResource(R.drawable.tamagochi_normal);
                    txtRecomendacion.setText(recomendacion[2]);
                }
            } else {
                if ((humidity > 80) && (humidity < 90)) {
                    imagenRecomendacion.setImageResource(R.drawable.tamagochi_normal);
                    txtRecomendacion.setText(recomendacion[1]);
                }
                else{
                    imagenRecomendacion.setImageResource(R.drawable.tamagochi_sad);
                    txtRecomendacion.setText(recomendacion[3]);
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected double celsiusToCentigrades(double temp) {
        return temp - 273.15;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openWeatherTask != null) openWeatherTask.cancel(true);
    }
}