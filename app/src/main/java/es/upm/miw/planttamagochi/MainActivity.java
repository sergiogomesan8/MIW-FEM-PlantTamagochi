package es.upm.miw.planttamagochi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.upm.miw.planttamagochi.service.device.ISpikeRESTAPIService;
import es.upm.miw.planttamagochi.fragments.Perfil;
import es.upm.miw.planttamagochi.model.PlantTamagochiModel;
import es.upm.miw.planttamagochi.pojo.AuthorizationBearer;
import es.upm.miw.planttamagochi.pojo.Co2;
import es.upm.miw.planttamagochi.pojo.Credentials;
import es.upm.miw.planttamagochi.pojo.Humidity;
import es.upm.miw.planttamagochi.pojo.Light;
import es.upm.miw.planttamagochi.pojo.Measurement;
import es.upm.miw.planttamagochi.pojo.SoilTemp1;
import es.upm.miw.planttamagochi.pojo.SoilTemp2;
import es.upm.miw.planttamagochi.pojo.Temperature;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static String LOG_TAG = "btb";

    private static final String API_LOGIN_POST = "https://thingsboard.cloud/api/auth/"; // Base url to obtain token
    private static final String API_BASE_GET = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/"; // Base url to obtain data
    private static final String BEARER = "Bearer ";
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";

    private ISpikeRESTAPIService apiService;

    private String sAuthBearerToken = "";

    PlantTamagochiModel plantTamagochiVM;
    FirebaseAuth Auth;
    FirebaseDatabase database;
    String firebaseUser;
    TextView txtsaludo, txtTemperatura, txtHumedad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plantTamagochiVM = new ViewModelProvider(this).get(PlantTamagochiModel.class);
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        this.crearObservadores();
        this.postBearerToken();

        firebaseUser = getIntent().getStringExtra("usuario");

        txtTemperatura = (TextView) findViewById(R.id.txtTemperaturaRes);
        txtHumedad = (TextView) findViewById(R.id.txtHumedadRes);
        txtsaludo = (TextView) findViewById(R.id.txtsaludo);
        txtsaludo.setText("Hola, " + firebaseUser);

        //Click Listeners
        findViewById(R.id.buttonVerPlanta).setOnClickListener(this);
        findViewById(R.id.buttonTransportar).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonVerPlanta) {
            this.getLastTelemetry();
        }
        if (i == R.id.buttonTransportar) {
            Intent intent = new Intent(this, TransportarPlantaActivity.class);
            startActivity(intent);
        }
    }


    private void crearObservadores() {
        plantTamagochiVM.getAuth().observe(
                this,
                new Observer<FirebaseAuth>() {
                    @Override
                    public void onChanged(FirebaseAuth firebaseAuth) {
                        marcarAuth(firebaseAuth);
                    }
                }
        );
    }

    private void marcarAuth(FirebaseAuth auth) {
        this.Auth = auth;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.opc_perfil:
                Perfil perfilDialog = new Perfil();
                perfilDialog.show(getSupportFragmentManager(), "frgPerfilDialog");
                return true;

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtNoAcceso),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }


    private void postBearerToken() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_LOGIN_POST)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        Credentials c = new Credentials("studentupm2022@gmail.com", "student");
        Call<AuthorizationBearer> call = iApi.postAuthorizationBearer(c);

        call.enqueue(new Callback<AuthorizationBearer>() {
            @Override
            public void onResponse(Call<AuthorizationBearer> call, Response<AuthorizationBearer> response) {
                AuthorizationBearer responseFromAPI = response.body();
                String responseString = "Response postBearerToken() Code : " + response.code() + "\nToken : " + responseFromAPI.getToken() + "\n" + "RefreshToken : " + responseFromAPI.getRefreshToken();
                Log.i(LOG_TAG, " response: " + responseString);
                sAuthBearerToken = responseFromAPI.getToken();
                Log.i(LOG_TAG, " granted AuthBearerToken: " + sAuthBearerToken);
            }

            @Override
            public void onFailure(Call<AuthorizationBearer> call, Throwable t) {
                Log.e(LOG_TAG, " error message: " + t.getMessage());
            }
        });
    }


    private void getLastTelemetry() {

        //https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/{{deviceId}}/values/timeseries?keys=co2&useStrictDataTypes=false
        String keys = "co2,humidity,light,soilTemp1,soilTemp2,temperature";
        String useStrictDataTypes = "false";

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(API_BASE_GET)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ISpikeRESTAPIService iApi = retrofit.create(ISpikeRESTAPIService.class);
        Log.i(LOG_TAG, " request params: |" + BEARER + sAuthBearerToken + "|" + DEVICE_ID + "|" + keys + "|" + useStrictDataTypes);
        Call<Measurement> call = iApi.getLastTelemetry(BEARER + sAuthBearerToken, DEVICE_ID, keys, useStrictDataTypes);


        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                Toast.makeText(MainActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                Measurement lm = response.body();

                if (lm == null) {
                    Log.i(LOG_TAG, " API returned empty values for measurement");
                } else {

                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");

                    List<Co2> lCo2 = lm.getCo2();
                    List<Humidity> lHum = lm.getHumidity();
                    List<Light> lLig = lm.getLight();
                    List<SoilTemp1> lST1 = lm.getSoilTemp1();
                    List<SoilTemp2> lST2 = lm.getSoilTemp2();
                    List<Temperature> lTem = lm.getTemperature();

                    Integer tempPlant = Integer.parseInt(lTem.get(0).getValue());
                    Integer humidity = Integer.parseInt(lHum.get(0).getValue());
                    txtTemperatura.setText(tempPlant.toString());
                    txtHumedad.setText(humidity.toString());
                    Log.i("temps", " TEMP - HUMEDAD: " + tempPlant + " - " + humidity);

                    if ((tempPlant > 18) && (tempPlant < 25)) {
                        txtTemperatura.setTextColor(Color.rgb(50,205,50));
                    } else {
                        txtTemperatura.setTextColor(Color.rgb(255,0,0));
                    }
                    if ((humidity > 80) && (humidity < 90)) {
                        txtHumedad.setTextColor(Color.rgb(50,205,50));
                    } else {
                        txtHumedad.setTextColor(Color.rgb(255,0,0));
                    }

                    DatabaseReference myRef = database.getReference("keys_api_plant" + " | " + dateFormat.format(date) + " | " + hourFormat.format(date));
                    Map<String, Object> m = new HashMap<>();
                    m.put("Co2", lCo2.get(0).getValue());
                    m.put("Hum", lHum.get(0).getValue());
                    m.put("Light", lLig.get(0).getValue());
                    m.put("SoilTemp1", lST1.get(0).getValue());
                    m.put("SoilTemp2", lST2.get(0).getValue());
                    m.put("Temp", lTem.get(0).getValue());
                    myRef.setValue(m);
                }

                String responseString = "Response getLastTelemetry() Code : " + response.code();
                Log.i(LOG_TAG, " response: " + responseString);
                Log.i(LOG_TAG, " response.body: " + lm.getCo2().get(0).getValue());
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.e(LOG_TAG, " error message: " + t.getMessage());
            }
        });

    }

    public void logOut() {
        Auth.signOut();
    }

}