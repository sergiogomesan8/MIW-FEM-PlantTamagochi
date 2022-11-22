package es.upm.miw.planttamagochi.service.openWeather;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import es.upm.miw.planttamagochi.pojo.Example;

public class LoadOpenWeatherTask extends AsyncTask<String, String, Example> {

    private static final String API_KEY_OPEN_WEATHER = "d7e3ddb6b3a92c0755579d7b29d05f37";

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Example doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        Example example = new Example();

        String ciudad = params[0];
        Log.e("Transp", "Ciudad: " + ciudad);
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + ciudad + "&appid=" + API_KEY_OPEN_WEATHER);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            Gson gson = new Gson();
            example = gson.fromJson(bufferedReader, Example.class);
            bufferedReader.close();
        } catch (Exception e) {
            Log.e("ERROR", "Error loading data");
        } finally {
            if (httpURLConnection != null) httpURLConnection.disconnect();
        }
        return example;
    }

    @Override
    protected void onPostExecute(Example result) {
        super.onPostExecute(result);
        //Toast.makeText(getContext(), "Datos de la ciudad descargados", Toast.LENGTH_SHORT).show();
    }

}
