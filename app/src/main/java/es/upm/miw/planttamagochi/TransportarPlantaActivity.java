package es.upm.miw.planttamagochi;

import androidx.appcompat.app.AppCompatActivity;

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

    private String[] ciudades = {"", "Madrid", "Barcelona", "Salamanca", "London"};
    private String[] recomendacion = {  "Es un lugar ideal, la planta se mantendrá correctamente",
                                        "Si lleva la planta tendrá que tener en cuenta alguna clave",
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

        ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ciudades);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        txtCiudad = (TextView)findViewById(R.id.txtCiudad);
        txtRecomendacion = (TextView)findViewById(R.id.txtRecomendacion);

        imagenRecomendacion = (ImageView)findViewById(R.id.imagenRecomendacion);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ciudad = spinner.getSelectedItem().toString();
                txtCiudad.setText(ciudad);
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
            if(ciudad!=""){
                this.loadOpenWeatherTask();
            }
            else{
                Toast.makeText(TransportarPlantaActivity.this, "Selecciona antes una ciudad", Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected void loadOpenWeatherTask(){
        openWeatherTask = new LoadOpenWeatherTask();
        try {
            recursoOpenWeather = openWeatherTask.execute(ciudad).get();
            //Log.e(LOG_TAG, "Valor temperatura: " + recursoOpenWeather.getName());

            Double temp = recursoOpenWeather.getMain().getTemp();
            temp = this.celsiusToCentigrades(temp);

            Log.e(LOG_TAG, "Valor temperatura: " + temp);

            if((temp > 20.0) && (temp < 25.0)){
                if(recursoOpenWeather.getMain().getHumidity() > 40){
                    imagenRecomendacion.setImageResource(R.drawable.tamagochi_happy);
                    imagenRecomendacion.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.green)));
                    txtRecomendacion.setText(recomendacion[0]);
                    txtRecomendacion.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.green)));
                }
                else{
                    imagenRecomendacion.setImageResource(R.drawable.tamagochi_happy);
                    imagenRecomendacion.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.ambar)));
                    txtRecomendacion.setText(recomendacion[1]);
                    txtRecomendacion.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.ambar)));
                }
            }
            else{
                imagenRecomendacion.setImageResource(R.drawable.tamagochi_sad);
                imagenRecomendacion.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.red)));
                txtRecomendacion.setText(recomendacion[2]);
                txtRecomendacion.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.red)));
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected double celsiusToCentigrades(double temp){
        return temp-273.15;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openWeatherTask != null) openWeatherTask.cancel(true);
    }
}