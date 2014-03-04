package turpin.mathieu.almanachdumarinbreton.overlay;

import java.util.Map;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.AddCommentListener;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.AddCommentServiceAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.CommentByIdServiceAsyncTask;
import turpin.mathieu.almanachdumarinbreton.forum.AddCommentDialog;
import turpin.mathieu.almanachdumarinbreton.forum.MyAccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class InfoOverlayItemDialog extends DialogFragment implements AddCommentListener{

	public interface InfoOverlayItemDialogListener {
		void commentByIdCentreInteret(int id);
	}

	private Activity activity;
	private double latitude;
	private double longitude;
	private MyAccountManager accountManager;

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
		accountManager = new MyAccountManager(activity);

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
				new CommentByIdServiceAsyncTask(activity,"Contact serveur").execute(params);
			}
		})
		.setNegativeButton("Ajouter un commentaire", new DialogInterface.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int id) {
				accountManager = new MyAccountManager(activity);
				if(accountManager.isLoggedIn()){
					Map<String,String> params = PoiController.getInstance().prepareGetByPosition(Double.toString(latitude), Double.toString(longitude));
					new AddCommentServiceAsyncTask(activity,"Contact serveur",InfoOverlayItemDialog.this).execute(params);
				}
				else{
					Toast.makeText(activity, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
				}
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	private void addComment(Integer idCentreUtilisateur, String nomCentreInteret) {
		int idUtilisateur = accountManager.getId();

		AddCommentDialog dialog = AddCommentDialog.getInstance(idUtilisateur,idCentreUtilisateur,latitude,longitude,nomCentreInteret);
		dialog.show(activity.getFragmentManager(), "AddCommentDialog");
	}

	@Override
	public void addCommentService(ServiceDTO poi) {
		addComment(poi.getId(),poi.getDescription());
	}

	@Override
	public void addCommentPoi(PoiDTO poi) {
		addComment(poi.getId(),poi.getType());
	}
}
