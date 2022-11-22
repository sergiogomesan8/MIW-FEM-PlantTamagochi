package es.upm.miw.planttamagochi.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.upm.miw.planttamagochi.model.PlantTamagochiModel;

public class Firebase {

    private final PlantTamagochiModel plantTamagochiModel;

    public Firebase(PlantTamagochiModel plantTamagochiModel) {
        this.plantTamagochiModel = plantTamagochiModel;
    }

    public FirebaseAuth getFirebaseAuth() {
        return plantTamagochiModel.getAuth().getValue();
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.plantTamagochiModel.setAuth(firebaseAuth);
    }

    public FirebaseUser getFirebaseUser(){
        FirebaseAuth firebaseAuth = this.getFirebaseAuth();
        return firebaseAuth.getCurrentUser();
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.plantTamagochiModel.setCurrentUser(firebaseUser);
    }

}
