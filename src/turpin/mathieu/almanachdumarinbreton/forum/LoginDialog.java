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

public class LoginDialog extends DialogFragment{

	public interface LoginDialogListener {
        void setIsLogin();
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Set the dialog title
		builder.setTitle(R.string.menu_login);

		final AccountManager accountManager = new AccountManager(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		View v = inflater.inflate(R.layout.login, null);
		builder.setView(v);

		//getView
		final EditText emailaddr = (EditText) v.findViewById(R.id.email);
		final EditText password = (EditText) v.findViewById(R.id.password);

		// Add action buttons
		builder.setPositiveButton(R.string.menu_login, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String emailText = emailaddr.getText().toString();
				String passwordText = password.getText().toString();
				accountManager.logIn(emailText);
				//Give result to currentActivity
				LoginDialogListener activity = (LoginDialogListener) getActivity();
				activity.setIsLogin();
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
