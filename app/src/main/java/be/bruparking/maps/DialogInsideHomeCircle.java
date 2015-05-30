package be.bruparking.maps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class DialogInsideHomeCircle extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		
		builder
			.setMessage("You are in home circle = FREE PARKING")
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// TODO:
				}
			});
			
		// Create the AlertDialog object and return it
		return builder.create();
	}


}
