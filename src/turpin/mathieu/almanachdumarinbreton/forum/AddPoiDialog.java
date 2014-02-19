package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AddPoiDialog extends DialogFragment{

	private Context context;
	private RadioButton remarqueButton;
	private RadioButton pecheButton;
	private RadioButton securiteButton;

	/**
	 * 
	 * @param title
	 * @return
	 */
	public static AddPoiDialog getInstance(double latitude, double longitude) {
		AddPoiDialog dialog = new AddPoiDialog();
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
		final double lat = getArguments().getDouble("lat");
		final double lon = getArguments().getDouble("lon");
		final AccountManager acc = new AccountManager(context);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set the dialog title
		builder.setTitle("Ajouter un point d'interet");

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.add_centre, null);
		builder.setView(v);

		//getView
		final EditText commentEdit = (EditText) v.findViewById(R.id.comment);
		final TextView positionTextView = (TextView) v.findViewById(R.id.position);
		final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioPartageGroup);

		remarqueButton = (RadioButton) v.findViewById(R.id.radioTypeRemarque);
		pecheButton = (RadioButton) v.findViewById(R.id.radioTypePeche);
		securiteButton = (RadioButton) v.findViewById(R.id.radioTypeSecurite);
		
		remarqueButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pecheButton.setChecked(false);
				securiteButton.setChecked(false);
			}
		});
		
		pecheButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				remarqueButton.setChecked(false);
				securiteButton.setChecked(false);
			}
		});
		
		securiteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				pecheButton.setChecked(false);
				remarqueButton.setChecked(false);
			}
		});

		//Init position
		String position = "Lat: " + Double.toString(lat) +"°, Lon: " + Double.toString(lon)+"°";
		positionTextView.setText(position);

		// Add action buttons
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int id) {
				int idUser = acc.getId();
				if(idUser == -1){
					Toast.makeText(context, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
				}
				else{
					String idUtilisateur = Integer.toString(idUser);
					String commentText = commentEdit.getText().toString();
					int idPartageChecked = radioGroup.getCheckedRadioButtonId();
					String partagePublic = Boolean.toString(isShared(idPartageChecked));
					String type = getTypeByButton();
					Map<String,String> params = PoiController.getInstance().prepareAddPoi(Double.toString(lat), Double.toString(lon),type,idUtilisateur,commentText,partagePublic);
					new addPoiAsyncTask().execute(params);
				}
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
		
		private String getTypeByButton(){
			if(pecheButton.isChecked()){
				return "peche";
			}
			else if(securiteButton.isChecked()){
				return "securite";
			}
			else{
				return "remarque";
			}
		}

		protected class addPoiAsyncTask extends AsyncTask<Map<String,String>, Void, PoiDTO>
		{
			protected ProgressDialog progressDialog;
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				progressDialog = new ProgressDialog(context);
				progressDialog.setTitle("Contact serveur");
				progressDialog.setMessage("En cours...");
				progressDialog.setCancelable(true);
				progressDialog.show();

			}

			@Override
			protected PoiDTO doInBackground(Map<String,String>... params)
			{
				PoiController poiController = PoiController.getInstance();
				try
				{
					if(params.length < 1){
						return null;
					}
					else{
						PoiDTO poi = poiController.addPoi(params[0]);
						return poi;
					}
				}
				catch (ClientProtocolException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute (PoiDTO poi) {
				progressDialog.dismiss();
				if(poi!=null){			
					Toast.makeText(context, "Enregistrement terminé", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
				}
			}

		}
	}
