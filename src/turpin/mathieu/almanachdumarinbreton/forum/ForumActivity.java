package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.description.DescriptionActivityWebLocal;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ForumActivity extends Activity{

	//Extra
	final int RESULT_IS_LOGIN = 0;
	final String EXTRA_PORT = "port_name";
	final String EXTRA_COURT_PORT = "port_court_name";
	final String EXTRA_MODE_MAP = "mode_map";

	private String courtNamePort ="";

	private Menu _menu;

	private AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum);

		Intent intent = getIntent();
		initIntentForActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.forum, menu);
		_menu = menu;

		Intent intent = getIntent();
		initIntentForMenu(intent);

		return true;
	}
	
	private void initIntentForActivity(Intent intent){
		if (intent != null) {
			String courtName = intent.getStringExtra(EXTRA_COURT_PORT);
			if(courtName != null){
				this.courtNamePort = courtName;
			}
		}
	}
	
	private void initIntentForMenu(Intent intent){
		if (intent != null) {
			int mode = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_offline);
			if(mode == R.id.map_online){
				_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_online);
				_menu.findItem(R.id.map_online).setEnabled(false);
				_menu.findItem(R.id.map_offline).setEnabled(true);
			}

			String port = intent.getStringExtra(EXTRA_PORT);
			if(port != null && port.equals(getResources().getString(R.string.menu_marina))){
				_menu.findItem(R.id.menu_port).setTitle(port);
			}
		}
		accountManager = new AccountManager(getApplicationContext());

		if(accountManager.isLoggedIn()){
			_menu.findItem(R.id.menu_compte).setTitle(getResources().getString(R.string.menu_compte));
		}
	}
	
	@Override
    protected void onNewIntent(Intent intent) 
    {
		super.onNewIntent(intent);
		initIntentForActivity(intent);
		initIntentForMenu(intent);
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
				new LoginDialog(this,_menu.findItem(R.id.menu_compte),this.accountManager);
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
}
