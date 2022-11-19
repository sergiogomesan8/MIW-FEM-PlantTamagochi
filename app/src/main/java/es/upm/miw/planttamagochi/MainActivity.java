package es.upm.miw.planttamagochi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upm.miw.planttamagochi.device.ISpikeRESTAPIService;
import es.upm.miw.planttamagochi.fragments.Perfil;
import es.upm.miw.planttamagochi.model.PlantTamagochiModel;
import es.upm.miw.planttamagochi.pojo.AuthorizationBearer;
import es.upm.miw.planttamagochi.pojo.Credentials;
import es.upm.miw.planttamagochi.pojo.Measurement;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static String LOG_TAG = "btb";
    public static final int NEW_ACTIVITY_REQUEST_CODE = 2022;

    private static final String API_LOGIN_POST = "https://thingsboard.cloud/api/auth/"; // Base url to obtain token
    private static final String API_BASE_GET = "https://thingsboard.cloud:443/api/plugins/telemetry/DEVICE/"; // Base url to obtain data
    //private static final String TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHVkZW50dXBtMjAyMkBnbWFpbC5jb20iLCJ1c2VySWQiOiI4NDg1OTU2MC00NzU2LTExZWQtOTQ1YS1lOWViYTIyYjlkZjYiLCJzY29wZXMiOlsiVEVOQU5UX0FETUlOIl0sImlzcyI6InRoaW5nc2JvYXJkLmNsb3VkIiwiaWF0IjoxNjY4ODQ4NTUyLCJleHAiOjE2Njg4NzczNTIsImZpcnN0TmFtZSI6IlN0dWRlbnQiLCJsYXN0TmFtZSI6IlVQTSIsImVuYWJsZWQiOnRydWUsImlzUHVibGljIjpmYWxzZSwiaXNCaWxsaW5nU2VydmljZSI6ZmFsc2UsInByaXZhY3lQb2xpY3lBY2NlcHRlZCI6dHJ1ZSwidGVybXNPZlVzZUFjY2VwdGVkIjp0cnVlLCJ0ZW5hbnRJZCI6ImUyZGQ2NTAwLTY3OGEtMTFlYi05MjJjLWY3NDAyMTlhYmNiOCIsImN1c3RvbWVySWQiOiIxMzgxNDAwMC0xZGQyLTExYjItODA4MC04MDgwODA4MDgwODAifQ.DLYNMLG9Yo3zHzf1WNTroykSGayDOPHbHkAK9iVyQqhHoWVW-FRx-to2N20ZaAnl4ScNEMCK0a0HrEfgVjvfGA";
    private static final String BEARER = "Bearer ";
    private static final String DEVICE_ID = "cf87adf0-dc76-11ec-b1ed-e5d3f0ce866e";
    private static final String USER_THB = "studentupm2022@gmail.com";
    private static final String PASS_THB = "student";

    private ISpikeRESTAPIService apiService;

    private String sAuthBearerToken ="";

    PlantTamagochiModel plantTamagochiVM;
    FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plantTamagochiVM = new ViewModelProvider(this).get(PlantTamagochiModel.class);
        Auth = FirebaseAuth.getInstance();
        //firebaseUser = plantTamagochiVM.getCurrentUser();
        //this.crearObservadores();

        this.postBearerToken();
        //this.getLastTelemetry();

        //Click Listeners
        findViewById(R.id.buttonVer).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonVer) {
            this.getLastTelemetry();
        }
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
        Credentials c = new Credentials("studentupm2022@gmail.com","student");
        Call<AuthorizationBearer> call = iApi.postAuthorizationBearer(c);

        call.enqueue(new Callback<AuthorizationBearer>() {
            @Override
            public void onResponse(Call<AuthorizationBearer> call, Response<AuthorizationBearer> response) {
                Toast.makeText(MainActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                AuthorizationBearer responseFromAPI = response.body();
                String responseString = "Response postBearerToken() Code : " + response.code() + "\nToken : " + responseFromAPI.getToken() + "\n" + "RefreshToken : " + responseFromAPI.getRefreshToken();
                Log.i(LOG_TAG, " response: "+responseString);
                sAuthBearerToken = responseFromAPI.getToken();
                Log.i(LOG_TAG, " granted AuthBearerToken: "+sAuthBearerToken);
            }

            @Override
            public void onFailure(Call<AuthorizationBearer> call, Throwable t) {
                Log.e(LOG_TAG, " error message: "+t.getMessage());
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
        Log.i(LOG_TAG, " request params: |"+ BEARER + sAuthBearerToken + "|"+ DEVICE_ID +"|"+keys+"|"+useStrictDataTypes);
        Call<Measurement> call = iApi.getLastTelemetry(BEARER + sAuthBearerToken, DEVICE_ID, keys, useStrictDataTypes);


        call.enqueue(new Callback<Measurement>() {
            @Override
            public void onResponse(Call<Measurement> call, Response<Measurement> response) {
                Toast.makeText(MainActivity.this, "Data posted to API", Toast.LENGTH_SHORT).show();
                Measurement lm = response.body();

                String responseString = "Response getLastTelemetry() Code : " + response.code();
                Log.i(LOG_TAG, " response: "+responseString);
                Log.i(LOG_TAG, " response.body: "+lm.getCo2().get(0).getValue());
            }

            @Override
            public void onFailure(Call<Measurement> call, Throwable t) {
                Log.e(LOG_TAG, " error message: "+t.getMessage());
            }
        });

    }

    public void logOut(){
        Auth = this.plantTamagochiVM.getAuth().getValue();
        Auth.signOut();
        plantTamagochiVM.setAuth(Auth);

        Log.i(LOG_TAG, "Log Out: success");
        Toast.makeText(MainActivity.this, "Log Out correct: ",
                Toast.LENGTH_SHORT).show();
    }
}