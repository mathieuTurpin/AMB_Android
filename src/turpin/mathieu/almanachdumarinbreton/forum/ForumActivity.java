package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.MyActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ForumActivity extends MyActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum);

		Button addComment = (Button) findViewById(R.id.boutonAdd);
		addComment.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(ForumActivity.this);
				// Set the dialog title
				builder.setTitle("Aide");
				builder.setMessage("Faite un clic long sur la carte pour ajouter un commentaire");

				// Add action buttons
				builder.setPositiveButton("Voir la carte", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//Go to map activity
						Intent intent = new Intent(ForumActivity.this, MainActivity.class);
						initIntent(intent);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(intent);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

				builder.create().show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.forum, menu);

		return super.onCreateOptionsMenu(menu);
	}
}
