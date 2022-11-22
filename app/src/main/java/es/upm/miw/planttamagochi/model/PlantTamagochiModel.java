package es.upm.miw.planttamagochi.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PlantTamagochiModel extends ViewModel {

    private MutableLiveData<FirebaseAuth> Auth;
    private FirebaseUser currentUser;

    public PlantTamagochiModel(){
        Auth = new MutableLiveData<FirebaseAuth>();
    }

    /**
     * @return Devuelve la autenticación en Firebase actual.
     */
    @NonNull
    public LiveData<FirebaseAuth> getAuth() {
        return Auth;
    }

    /**
     * Establece la autenticación en Firebase
     * @param auth
     */

    public void setAuth(FirebaseAuth auth) {
        this.Auth.setValue(auth);
    }
}
