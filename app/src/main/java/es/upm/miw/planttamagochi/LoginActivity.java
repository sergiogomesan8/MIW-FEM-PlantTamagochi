package es.upm.miw.planttamagochi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upm.miw.planttamagochi.model.PlantTamagochiModel;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = "Auth";

    private FirebaseAuth Auth;

    private EditText etUser;
    private EditText etPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Auth = FirebaseAuth.getInstance();


        //Fields
        etUser = findViewById(R.id.fieldUser);
        etPassword = findViewById(R.id.fieldPassword);

        //Click Listeners
        findViewById(R.id.buttonRegistro).setOnClickListener(this);
        findViewById(R.id.buttonSignIn).setOnClickListener(this);

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
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.field_required));
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    private void signInWithCredentials() {
        if (!validateLinkForm()) {
            return;
        }

        // Get email and password from form
        String user = etUser.getText().toString();
        String password = etPassword.getText().toString();

        // Create EmailAuthCredential with email and password
        AuthCredential credential = EmailAuthProvider.getCredential(user, password);
        Intent intent2 = new Intent(this, MainActivity.class);

        // [START signin_with_email_and_password]
        Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(LOG_TAG, "signInWithCredentials:success");

                            FirebaseUser firebaseUser = Auth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Authentication correct: " + firebaseUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();

                            intent2.putExtra("usuario", firebaseUser.getEmail());
                            startActivity(intent2);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(LOG_TAG, "signInWithCredentials:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END signin_with_email_and_password]
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {
            this.signInWithCredentials();
        }
        if (i == R.id.buttonRegistro) {
            Intent intent = new Intent(this, RegistroActivity.class);
            startActivity(intent);
        }
    }
}
