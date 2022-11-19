package es.upm.miw.planttamagochi.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import es.upm.miw.planttamagochi.LoginActivity;
import es.upm.miw.planttamagochi.MainActivity;
import es.upm.miw.planttamagochi.R;

public class Perfil extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final MainActivity mainActivity = (MainActivity) getActivity();
        Intent intent = new Intent(getContext(), LoginActivity.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.txtOpcTituloCerrarSesion))
                .setMessage(getString(R.string.txtOpcCerrarSesion))
                .setPositiveButton(
                        getString(R.string.txtYesDialog),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mainActivity.logOut();
                                startActivity(intent);
                            }
                        }
                )
                .setNegativeButton(
                        getString(R.string.txtNoDialog),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }
                );

        return builder.create();
    }
}
