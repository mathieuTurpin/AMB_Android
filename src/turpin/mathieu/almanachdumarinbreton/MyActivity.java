package turpin.mathieu.almanachdumarinbreton;

import turpin.mathieu.almanachdumarinbreton.forum.AccountManager;
import turpin.mathieu.almanachdumarinbreton.forum.LoginDialog;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public abstract class MyActivity extends Activity implements LoginDialog.LoginDialogListener{

	//Extra
	public static final int RESULT_IS_LOGIN = 0;
	public static final String EXTRA_PORT = "port_name";
	public static final String EXTRA_COURT_PORT = "port_court_name";
	public static final String EXTRA_MODE_MAP = "mode_map";
	
	protected int mode;
	protected String courtNamePort ="";
	protected String port ="";
	
	protected Menu _menu;

	protected AccountManager accountManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountManager = new AccountManager(getApplicationContext());
		
		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null) {
			this.mode = savedInstanceState.getInt(EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = savedInstanceState.getString(EXTRA_COURT_PORT);
			this.port = savedInstanceState.getString(EXTRA_PORT);
		}
		else{
			initIntentForActivity(intent);
		}
	}
	
	protected void initIntentForActivity(Intent intent){
		if (intent != null) {
			//Get parameters
			this.mode = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = intent.getStringExtra(EXTRA_COURT_PORT);
			this.port = intent.getStringExtra(EXTRA_PORT);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		_menu = menu;
		initMenu();

		return true;
	}
	
	protected void initMenu(){
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

		initIntentForActivity(intent);
		if(_menu != null){
			initMenu();
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
	
	protected void initIntent(Intent intent){
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
