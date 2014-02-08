package turpin.mathieu.almanachdumarinbreton.overlay;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.CentreInteretController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CentreInteretDTO;
import turpin.mathieu.almanachdumarinbreton.forum.AccountManager;
import turpin.mathieu.almanachdumarinbreton.forum.AddCommentDialog;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class InfoOverlayItemDialog extends DialogFragment {
	
	public interface InfoOverlayItemDialogListener {
		void commentByIdCentreInteret(int id);
	}
	
	private Activity activity;
	private double latitude;
	private double longitude;

	public static InfoOverlayItemDialog getInstance(String title, String message,double latitude, double longitude) {
		InfoOverlayItemDialog dialog = new InfoOverlayItemDialog();
		Bundle args = new Bundle();
		args.putString("title", title);
		args.putString("message", message);
		args.putDouble("lat", latitude);
		args.putDouble("lon", longitude);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = getActivity();

		//Get argument
		String title = getArguments().getString("title");
		String message = getArguments().getString("message");
		this.latitude = getArguments().getDouble("lat");
		this.longitude = getArguments().getDouble("lon");

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setIcon(android.R.drawable.ic_menu_info_details);
		builder.setTitle(title);
		builder.setMessage(message);

		// Add action buttons
		builder.setPositiveButton("Voir les commentaires", new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int id) {
				Map<String,String> params = CentreInteretController.getInstance().prepareGetByPosition(Double.toString(latitude), Double.toString(longitude));
				new commentByIdCentreInteretAsyncTask().execute(params);
			}
		})
		.setNegativeButton("Ajouter un commentaire", new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int id) {
				Map<String,String> params = CentreInteretController.getInstance().prepareGetByPosition(Double.toString(latitude), Double.toString(longitude));
				new addCommentAsyncTask().execute(params);
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	protected abstract class getIdCentreInteretAsyncTask extends AsyncTask<Map<String,String>, Void, CentreInteretDTO>
	{
		protected ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle("Contact serveur");
			progressDialog.setMessage("En cours...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected CentreInteretDTO doInBackground(Map<String,String>... params)
		{
			CentreInteretController centreInteretController = CentreInteretController.getInstance();
			try
			{
				if(params.length < 1){
					return null;
				}
				else{
					CentreInteretDTO centreInteret = centreInteretController.findCentreInteretByPosition(params[0]);
					return centreInteret;
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

	}
	
	protected class commentByIdCentreInteretAsyncTask extends getIdCentreInteretAsyncTask{
		@Override
		protected void onPostExecute (CentreInteretDTO centreInteret) {
			progressDialog.dismiss();
			if(centreInteret != null){
				InfoOverlayItemDialogListener mActivity = (InfoOverlayItemDialogListener) activity;
				mActivity.commentByIdCentreInteret(centreInteret.getId().intValue());
			}
			else{
				Toast.makeText(activity, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	protected class addCommentAsyncTask extends getIdCentreInteretAsyncTask{
		@Override
		protected void onPostExecute (CentreInteretDTO centreInteret) {
			progressDialog.dismiss();
			if(centreInteret != null){
				AccountManager accountManager = new AccountManager(activity);
				if(accountManager.isLoggedIn()){
					int idUtilisateur = accountManager.getId();
					int idCentreUtilisateur = centreInteret.getId();
					String nomCentreInteret = centreInteret.getNom();
					AddCommentDialog dialog = AddCommentDialog.getInstance(idUtilisateur,idCentreUtilisateur,latitude,longitude,nomCentreInteret);
					dialog.show(activity.getFragmentManager(), "AddCommentDialog");
				}
				else{
					Toast.makeText(activity, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
				}
			}
			else{
				Toast.makeText(activity, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
