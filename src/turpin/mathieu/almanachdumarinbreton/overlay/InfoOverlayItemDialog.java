package turpin.mathieu.almanachdumarinbreton.overlay;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;
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
				Map<String,String> params = PoiController.getInstance().prepareGetByPosition(Double.toString(latitude), Double.toString(longitude));
				new commentByIdServiceAsyncTask().execute(params);
			}
		})
		.setNegativeButton("Ajouter un commentaire", new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int id) {
				Map<String,String> params = PoiController.getInstance().prepareGetByPosition(Double.toString(latitude), Double.toString(longitude));
				new addCommentServiceAsyncTask().execute(params);
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	protected abstract class getIdPoiAsyncTask extends AsyncTask<Map<String,String>, Void, PoiDTO>
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
		protected PoiDTO doInBackground(Map<String,String>... params)
		{
			PoiController poiController = PoiController.getInstance();
			try
			{
				if(params.length < 1){
					return null;
				}
				else{
					PoiDTO poi = poiController.findPoiByPosition(params[0]);
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

	}
	
	protected class addCommentPoiAsyncTask extends getIdPoiAsyncTask{

		private AccountManager accountManager;
		@Override
		protected void onPreExecute()
		{
			accountManager = new AccountManager(activity);
			if(accountManager.isLoggedIn()){
				super.onPreExecute();
			}
			else{
				cancel(true);
				Toast.makeText(activity, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPostExecute (PoiDTO poi) {
			progressDialog.dismiss();
			if(poi != null){
				int idUtilisateur = accountManager.getId();
				int idCentreUtilisateur = poi.getId();

				String nomCentreInteret = poi.getType();
				AddCommentDialog dialog = AddCommentDialog.getInstance(idUtilisateur,idCentreUtilisateur,latitude,longitude,nomCentreInteret);
				dialog.show(activity.getFragmentManager(), "AddCommentDialog");
			}
			else{
				Toast.makeText(activity, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected class commentByIdPoiAsyncTask extends getIdPoiAsyncTask{
		@Override
		protected void onPostExecute (PoiDTO centreInteret) {
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

	protected abstract class getIdServiceAsyncTask extends AsyncTask<Map<String,String>, Void, ServiceDTO>{
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
		protected ServiceDTO doInBackground(Map<String,String>... params)
		{
			PoiController poiController = PoiController.getInstance();
			try
			{
				if(params.length < 1){
					return null;
				}
				else{
					ServiceDTO service = poiController.findServiceByPosition(params[0]);
					return service;
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

	protected class commentByIdServiceAsyncTask extends getIdServiceAsyncTask{
		@Override
		protected void onPostExecute (ServiceDTO centreInteret) {
			progressDialog.dismiss();
			if(centreInteret != null){
				int nbComment = centreInteret.getCommentaires().size();
				if(nbComment<1){
					Toast.makeText(activity, "Aucun commentaire", Toast.LENGTH_SHORT).show();
				}
				else{
					InfoOverlayItemDialogListener mActivity = (InfoOverlayItemDialogListener) activity;
					mActivity.commentByIdCentreInteret(centreInteret.getId().intValue());
				}

			}
			else{
				Toast.makeText(activity, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected class addCommentServiceAsyncTask extends getIdServiceAsyncTask{

		private AccountManager accountManager;
		@Override
		protected void onPreExecute()
		{
			accountManager = new AccountManager(activity);
			if(accountManager.isLoggedIn()){
				super.onPreExecute();
			}
			else{
				cancel(true);
				Toast.makeText(activity, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPostExecute (ServiceDTO poi) {
			progressDialog.dismiss();
			if(poi != null){
				int idUtilisateur = accountManager.getId();
				int idCentreUtilisateur = poi.getId();

				String nomCentreInteret = poi.getType();
				AddCommentDialog dialog = AddCommentDialog.getInstance(idUtilisateur,idCentreUtilisateur,latitude,longitude,nomCentreInteret);
				dialog.show(activity.getFragmentManager(), "AddCommentDialog");
			}
			else{
				Toast.makeText(activity, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
