package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddCentreInteretDialog extends DialogFragment{
	
	private Context context;
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public static AddCentreInteretDialog getInstance(double latitude, double longitude) {
		AddCentreInteretDialog dialog = new AddCentreInteretDialog();
        Bundle args = new Bundle();
        args.putDouble("lat", latitude);
        args.putDouble("lon", longitude);
        dialog.setArguments(args);
        return dialog;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		context = getActivity();
		//Get argument
		double lat = getArguments().getDouble("lat");
		double lon = getArguments().getDouble("lon");
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set the dialog title
		builder.setTitle("Ajouter un centre d'interet");

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.add_centre, null);
		builder.setView(v);

		//getView
		final EditText commentEdit = (EditText) v.findViewById(R.id.comment);
		final TextView positionTextView = (TextView) v.findViewById(R.id.position);

		//Init position
		String position = "Lat: " + Double.toString(lat) +"°, Lon: " + Double.toString(lon)+"°";
		positionTextView.setText(position);
		
		// Add action buttons
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String commentText = commentEdit.getText().toString();
				String positionText = positionTextView.getText().toString();
				Toast.makeText(context, "Non implémenté", Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
