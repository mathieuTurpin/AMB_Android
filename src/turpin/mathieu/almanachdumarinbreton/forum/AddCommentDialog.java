package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class AddCommentDialog extends DialogFragment{
	
	/**
	 * 
	 * @param title
	 * @return
	 */
	public static AddCommentDialog getInstance(double latitude, double longitude) {
		AddCommentDialog dialog = new AddCommentDialog();
        Bundle args = new Bundle();
        args.putDouble("lat", latitude);
        args.putDouble("lon", longitude);
        dialog.setArguments(args);
        return dialog;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Get argument
		double lat = getArguments().getDouble("lat");
		double lon = getArguments().getDouble("lon");
		
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set the dialog title
		builder.setTitle("Ajouter un commentaire");

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.add_comment, null);
		builder.setView(v);

		//getView
		final EditText commentEdit = (EditText) v.findViewById(R.id.comment);
		final TextView positionTextView = (TextView) v.findViewById(R.id.position);
		final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioPartageGroup);

		//Init position
		String position = "Lat: " + Double.toString(lat) +"°, Lon: " + Double.toString(lon)+"°";
		positionTextView.setText(position);
		
		// Add action buttons
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String commentText = commentEdit.getText().toString();
				String positionText = positionTextView.getText().toString();
				int idButtonChecked = radioGroup.getCheckedRadioButtonId();
				boolean sharedMode = isShared(idButtonChecked);
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

	private boolean isShared(int id){
		switch(id){
		case R.id.radioPartagePublic:
			return true;
		case R.id.radioPartagePrivate:
			return false;
		default:
			return false;
		}
	}
}
