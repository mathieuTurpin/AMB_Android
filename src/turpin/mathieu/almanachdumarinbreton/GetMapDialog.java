package turpin.mathieu.almanachdumarinbreton;

import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.asynctask.GetMapAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.GetMapAsyncTask.GetMapListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

public class GetMapDialog extends DialogFragment implements GetMapListener{
	private Activity activity;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		this.activity = getActivity();
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		// Set the dialog title
		builder.setTitle("Obtenir la carte");
		
		builder.setMessage("Souhaitez-vous télécharger la carte pour disposer du mode offline et pouvoir visualiser la carte sans Internet par la suite?");

		// Add action buttons
		builder.setPositiveButton("Télécharger la carte", new DialogInterface.OnClickListener() {
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
		final GetMapDialog myDialog = GetMapDialog.this;
		
		AlertDialog d = (AlertDialog)getDialog();
		if(d != null)
		{
			Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
			positiveButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
					String cacheDirectoryPath = externalStorageDirectory + MainActivity.PATH_MAP_FILE;
					new GetMapAsyncTask(activity,myDialog).execute(MainActivity.URL_BRETAGNE_MAP,cacheDirectoryPath);
				}
			});
		}
	}

	@Override
	public void setConnectionOffline() {
		dismiss();
	}
}