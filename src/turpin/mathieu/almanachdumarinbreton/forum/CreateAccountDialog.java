package turpin.mathieu.almanachdumarinbreton.forum;

import java.util.Map;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur.CreateAccountAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur.CreateAccountAsyncTask.CreateAccountListener;
import turpin.mathieu.almanachdumarinbreton.forum.LoginDialog.LoginDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccountDialog extends DialogFragment implements CreateAccountListener{
	private Activity activity;
	private EditText nom;
	private EditText prenom;
	private EditText emailaddr;
	private EditText password;
	private EditText description;

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
					new CreateAccountAsyncTask(activity,"Création du compte",CreateAccountDialog.this).execute(params);
				}
			});
		}
	}

	@Override
	public void createAccount(UtilisateurDTO user) {
		//Save parameters of this user
		AccountManager accountManager = new AccountManager(activity);
		accountManager.logIn(user);

		//Give result to activity
		LoginDialogListener mActivity = (LoginDialogListener) activity;
		mActivity.setIsLogin();
		dismiss();
	}
}