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

}
