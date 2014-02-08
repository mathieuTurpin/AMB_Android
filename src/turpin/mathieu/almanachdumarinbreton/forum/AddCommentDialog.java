package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.CentreInteretController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentaireDTO;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AddCommentDialog extends DialogFragment{
	private Activity activity;
	
	public static AddCommentDialog getInstance(int idUtilisateur, int idCentreInteret, double latitude, double longitude, String nomCentreInteret) {
		AddCommentDialog dialog = new AddCommentDialog();
        Bundle args = new Bundle();
        args.putInt("idUtilisateur", idUtilisateur);
        args.putInt("idCentreInteret", idCentreInteret);
        args.putDouble("lat", latitude);
        args.putDouble("lon", longitude);
        args.putString("nomCentreInteret", nomCentreInteret);
        dialog.setArguments(args);
        return dialog;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = getActivity();
		
		//Get argument
		String nomCentreInteret = getArguments().getString("nomCentreInteret");
		final int id_utilisateur = getArguments().getInt("idUtilisateur");
		final int id_centre_interet = getArguments().getInt("idCentreInteret");
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
		final TextView nomCentreInteretText = (TextView) v.findViewById(R.id.centreInteret);
		final EditText commentEdit = (EditText) v.findViewById(R.id.comment);
		final TextView positionTextView = (TextView) v.findViewById(R.id.position);
		final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioPartageGroup);
		nomCentreInteretText.setText("Nom du centre d'interet : "+nomCentreInteret);
		
		//Init position
		String position = "Lat: " + Double.toString(lat) +"°, Lon: " + Double.toString(lon)+"°";
		positionTextView.setText(position);
		
		// Add action buttons
		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int id) {	
				String idUtilisateur = Integer.toString(id_utilisateur);
				String idCentreInteret = Integer.toString(id_centre_interet);
				String contenu = commentEdit.getText().toString();
				int idButtonChecked = radioGroup.getCheckedRadioButtonId();
				String partagePublic = Boolean.toString(isShared(idButtonChecked));
				
				Map<String,String> formValues = CentreInteretController.getInstance().prepareAddComment(idUtilisateur, idCentreInteret, contenu, partagePublic);
				new AddCommentAsyncTask().execute(formValues);
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
	
	protected class AddCommentAsyncTask extends AsyncTask<Map<String,String>, Void, CommentaireDTO>
	{
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle("Ajout de commentaire");
			progressDialog.setMessage("En cours...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected CommentaireDTO doInBackground(Map<String,String>... params)
		{
			CentreInteretController centreInteretController = CentreInteretController.getInstance();
			try
			{
				if(params.length < 1){
					return null;
				}
				else{
					CommentaireDTO commentaire = centreInteretController.addComment(params[0]);
					return commentaire;
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
		protected void onPostExecute (CommentaireDTO user) {
			progressDialog.dismiss();
			if(user != null){
				Toast.makeText(activity, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(activity, "Erreur lors de l'ajout du commentaire", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
