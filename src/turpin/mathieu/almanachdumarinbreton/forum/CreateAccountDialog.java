package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountDialog extends DialogFragment{
	private Activity activity;
	private EditText nom;
	private EditText prenom;
	private EditText emailaddr;
	private EditText password;
	private EditText description;

	public interface LoginDialogListener {
		void setIsLogin();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = getActivity();
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		// Set the dialog title
		builder.setTitle("Creer un compte");

		// Get the layout inflater
		LayoutInflater inflater = activity.getLayoutInflater();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.create_account, null);
		builder.setView(v);

		//getView
		nom = (EditText) v.findViewById(R.id.nom);
		prenom = (EditText) v.findViewById(R.id.prenom);
		emailaddr = (EditText) v.findViewById(R.id.email);
		password = (EditText) v.findViewById(R.id.password);
		description = (EditText) v.findViewById(R.id.description);

		// Add action buttons
		builder.setPositiveButton(R.string.menu_login, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//Do nothing here because we override this button later to change the close behaviour. 
				//However, we still need this because on older versions of Android unless we 
				//pass a handler the button doesn't get instantiated
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				dialog.cancel();
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void onStart()
	{
		super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
		AlertDialog d = (AlertDialog)getDialog();
		if(d != null)
		{
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener()
			{
				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v)
				{
					String nomText =nom.getText().toString();
					String prenomText = prenom.getText().toString();
					String emailText = emailaddr.getText().toString();
					String passwordText = password.getText().toString();
					String descriptionText = description.getText().toString();
					
					Map<String,String> params = UtilisateurController.getInstance().prepareCreateAccount(nomText,prenomText,emailText, passwordText,descriptionText);					
					new AuthentificationAsyncTask().execute(params);
				}
			});
		}
	}

	protected class AuthentificationAsyncTask extends AsyncTask<Map<String,String>, Void, UtilisateurDTO>
	{
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog = new ProgressDialog(activity);
			progressDialog.setTitle("Cr�ation du compte");
			progressDialog.setMessage("En cours...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected UtilisateurDTO doInBackground(Map<String,String>... params)
		{
			UtilisateurController utilisateurController = UtilisateurController.getInstance();
			try
			{
				if(params.length < 1){
					return null;
				}
				else{
					UtilisateurDTO utilisateur = utilisateurController.createAccount(params[0]);
					return utilisateur;
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
		protected void onPostExecute (UtilisateurDTO user) {
			progressDialog.dismiss();
			if(user != null){
				//Save parameters of this user
				AccountManager accountManager = new AccountManager(activity);
				accountManager.logIn(user);

				//Give result to currentActivity
				LoginDialogListener mActivity = (LoginDialogListener) activity;
				mActivity.setIsLogin();
			}
			else{
				Toast.makeText(activity, "Erreur lors de la cr�ation du compte", Toast.LENGTH_SHORT).show();
			}
		}
	}
}