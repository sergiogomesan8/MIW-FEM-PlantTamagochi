package es.upm.miw.planttamagochi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upm.miw.planttamagochi.model.PlantTamagochiModel;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "Auth";

    private FirebaseAuth Auth;

    private EditText etUser;
    private EditText etPassword, etPassword2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Fields
        etUser = findViewById(R.id.fieldUser);
        etPassword = findViewById(R.id.fieldPassword);
        etPassword2 = findViewById(R.id.fieldPassword2);

        //Click Listeners
        findViewById(R.id.buttonRegistro).setOnClickListener(this);

        //Initialize Firebase Auth
        Auth = FirebaseAuth.getInstance();
    }

    private boolean validateLinkForm() {
        boolean valid = true;

        String user = etUser.getText().toString();
        if (TextUtils.isEmpty(user)) {
            etUser.setError(getString(R.string.field_required));
            valid = false;
        } else {
            etUser.setError(null);
        }

        String password = etPassword.getText().toString();
        String password2 = etPassword2.getText().toString();
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password2) ) {
            if(TextUtils.isEmpty(password)){
                etPassword.setError(getString(R.string.field_required));
            }
            if(TextUtils.isEmpty(password2)){
                etPassword2.setError(getString(R.string.field_required));
            }
            valid = false;
        } else {
            if(password.equals(password2)){
                etPassword.setError(null);
                etPassword2.setError(null);
                valid = true;
            }
            else {
                Log.w("Contraseña", "pass - pass2: " + password + " - " + password2);
                etPassword.setError(getString(R.string.field_not_equal));
                etPassword2.setError(getString(R.string.field_not_equal));
                valid = false;
            }
        }
        return valid;
    }

    private void createUserWithEmailAndPassword() {
        if (!validateLinkForm()) {
            return;
        }

        String user = etUser.getText().toString();
        String password = etPassword.getText().toString();

        Intent intent = new Intent(this, LoginActivity.class);

        Auth.createUserWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(LOG_TAG, "createUserWithEmailAndPassword:success");

                            FirebaseUser firebaseUser = Auth.getCurrentUser();
                            Toast.makeText(RegistroActivity.this, "Usuario creado correctamente: " + firebaseUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "createUserWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, "Usuario no creado: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonRegistro) {
            this.createUserWithEmailAndPassword();
        }
    }
}
