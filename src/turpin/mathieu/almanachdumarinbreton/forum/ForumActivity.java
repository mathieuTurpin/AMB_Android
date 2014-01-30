package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.description.DescriptionActivityWebLocal;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ForumActivity extends Activity implements LoginDialog.LoginDialogListener{

	//Extra
	final int RESULT_IS_LOGIN = 0;
	final String EXTRA_PORT = "port_name";
	final String EXTRA_COURT_PORT = "port_court_name";
	final String EXTRA_MODE_MAP = "mode_map";

	private int mode;
	private String courtNamePort ="";
	private String port ="";

	private Menu _menu;

	private AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum);

		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null && intent.getExtras() == null) {
			this.mode = savedInstanceState.getInt(EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = savedInstanceState.getString(EXTRA_COURT_PORT);
			this.port = savedInstanceState.getString(EXTRA_PORT);
		}
		else{
			initIntentForActivity(intent);
		}

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
		_menu = menu;

		initMenu();

		return true;
	}
	
	private void initIntentForActivity(Intent intent){
		if (intent != null) {
			//Get parameters
			this.mode = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = intent.getStringExtra(EXTRA_COURT_PORT);
			this.port = intent.getStringExtra(EXTRA_PORT);
		}
	}
	
	private void initMenu(){
		if(this.mode == R.id.map_online){
			_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_online);
			_menu.findItem(R.id.map_online).setEnabled(false);
			_menu.findItem(R.id.map_offline).setEnabled(true);
		}
		else{
			_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_offline);
			_menu.findItem(R.id.map_online).setEnabled(true);
			_menu.findItem(R.id.map_offline).setEnabled(false);
		}
		
		if(this.port != null && port.equals(getResources().getString(R.string.menu_marina))){
			_menu.findItem(R.id.menu_port).setTitle(port);
		}
		else{
			_menu.findItem(R.id.menu_port).setTitle(R.string.menu_port);
		}
		
		accountManager = new AccountManager(getApplicationContext());

		if(accountManager.isLoggedIn()){
			_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_compte);
		}
		else{
			_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_login);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) 
	{
		super.onNewIntent(intent);
		//To check if is not orientation change
		if(intent.getExtras() != null){
			initIntentForActivity(intent);
			if(_menu != null){
				initMenu();
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		savedInstanceState.putString(EXTRA_COURT_PORT, this.courtNamePort);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			savedInstanceState.putInt(EXTRA_MODE_MAP, R.id.map_online);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_marina:
			// Button behavior "Marina"
			_menu.findItem(R.id.menu_port).setTitle(item.getTitle().toString());
			this.courtNamePort = getResources().getString(R.string.name_marina);
			return true;
		case R.id.map:
			intent = new Intent(ForumActivity.this, MainActivity.class);
			initIntent(intent);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.map_description:
			// Button behavior "Map Decription"
			// if no port is selected
			String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
			if(namePort.equals(getResources().getString(R.string.menu_port))){
				Toast.makeText(ForumActivity.this, R.string.error_missing_port, Toast.LENGTH_SHORT).show();
				return true;
			}
			intent = new Intent(ForumActivity.this, DescriptionActivityWebLocal.class);
			initIntent(intent);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.map_offline:
			// Button behavior "Map offline"
			_menu.findItem(R.id.map_online).setEnabled(true);
			_menu.findItem(R.id.map_offline).setEnabled(false);
			_menu.findItem(R.id.menu_connexion).setTitle(item.getTitle());
			return true;
		case R.id.map_online:
			// Button behavior "Map Online"
			_menu.findItem(R.id.map_online).setEnabled(false);
			_menu.findItem(R.id.map_offline).setEnabled(true);
			_menu.findItem(R.id.menu_connexion).setTitle(item.getTitle());
			return true;
		case R.id.menu_compte:
			// Button behavior "Compte"
			if(accountManager.isLoggedIn()){
				intent = new Intent(ForumActivity.this, AccountActivity.class);
				initIntent(intent);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent, RESULT_IS_LOGIN);
			}
			else{
				// Create an instance of the dialog fragment and show it
				LoginDialog dialog = new LoginDialog();
				dialog.show(getFragmentManager(), "LoginDialog");
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initIntent(Intent intent){
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			intent.putExtra(EXTRA_MODE_MAP, R.id.map_online);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case RESULT_IS_LOGIN:
			switch(resultCode){
			//Logout
			case Activity.RESULT_CANCELED:
				_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_login);
				break;
				//Always login
			case Activity.RESULT_OK:
				//Nothing to do
				break;
			}
			break;
		default:
			return;
		}
	}

	@Override
	public void setIsLogin() {
		_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_compte);
	}
}
