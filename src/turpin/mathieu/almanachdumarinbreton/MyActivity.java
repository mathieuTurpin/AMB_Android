package turpin.mathieu.almanachdumarinbreton;

import turpin.mathieu.almanachdumarinbreton.description.DescriptionActivityWebLocal;
import turpin.mathieu.almanachdumarinbreton.forum.AccountActivity;
import turpin.mathieu.almanachdumarinbreton.forum.ForumActivity;
import turpin.mathieu.almanachdumarinbreton.forum.LoginDialog;
import turpin.mathieu.almanachdumarinbreton.forum.MyAccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class MyActivity extends Activity implements LoginDialog.LoginDialogListener{

	//Extra
	public static final int RESULT_IS_LOGIN = 0;
	public static final String EXTRA_PORT = "port_name";
	public static final String EXTRA_COURT_PORT = "port_court_name";
	public static final String EXTRA_MODE_MAP = "mode_map";
	
	public static final String ARROW = " ->";

	protected int mode;
	protected String courtNamePort ="";
	protected String port ="";

	protected Menu _menu;

	protected MyAccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountManager = new MyAccountManager(getApplicationContext());

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
			this.mode = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_online);
			this.courtNamePort = intent.getStringExtra(EXTRA_COURT_PORT);
			this.port = intent.getStringExtra(EXTRA_PORT);
		}
		else{
			this.mode = R.id.map_online;
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
			String mode = getResources().getString(R.string.menu_online);
			_menu.findItem(R.id.menu_connexion).setTitle(mode + MyActivity.ARROW);
			
			_menu.findItem(R.id.map_online).setEnabled(false);
			_menu.findItem(R.id.map_offline).setEnabled(true);
		}
		else{
			String mode = getResources().getString(R.string.menu_offline);
			_menu.findItem(R.id.menu_connexion).setTitle(mode + MyActivity.ARROW);
			
			_menu.findItem(R.id.map_online).setEnabled(true);
			_menu.findItem(R.id.map_offline).setEnabled(false);
		}

		if(this.port != null && port.equals(getResources().getString(R.string.menu_marina))){
			_menu.findItem(R.id.menu_port).setTitle(port + MyActivity.ARROW);
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
		savedInstanceState.putString(EXTRA_PORT, this.port);
		savedInstanceState.putString(EXTRA_COURT_PORT, this.courtNamePort);
		//String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		//if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			savedInstanceState.putInt(EXTRA_MODE_MAP, this.mode);
		//}
	}

	protected void initIntent(Intent intent){
		intent.putExtra(EXTRA_PORT, this.port);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		//String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		//if(mode_connexion.equals(getResources().getString(R.string.menu_online) + MyActivity.ARROW)){
			intent.putExtra(EXTRA_MODE_MAP, this.mode);
		//}
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
		Toast.makeText(this, "Authenfication réussie", Toast.LENGTH_SHORT).show();
		_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_compte);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_marina:
			goToPort(R.id.menu_marina);
			return true;
		case R.id.map:
			goToMap();
			return true;
		case R.id.map_description:
			goToDescription();
			return true;
		case R.id.menu_forum:
			goToForum();
			return true;
		case R.id.map_offline:
			setConnectionMode(false,item.getTitle());
			return true;
		case R.id.map_online:
			setConnectionMode(true,item.getTitle());
			return true;
		case R.id.menu_compte:
			if(accountManager.isLoggedIn()){
				goToAccount();
			}
			else{
				goToLogin();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void goToPort(int idPort){
		String namePort = _menu.findItem(idPort).getTitle().toString();
		this.port = namePort;
		_menu.findItem(R.id.menu_port).setTitle(namePort + MyActivity.ARROW);
		switch(idPort){
		case R.id.menu_marina:
			this.courtNamePort = getResources().getString(R.string.name_marina);
			break;
		}
	}

	private void goToMap(){
		Intent intent = new Intent(this, MainActivity.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void goToDescription(){
		// if no port is selected
		//String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
		if(this.port.equals(getResources().getString(R.string.menu_port))){
			Toast.makeText(this, R.string.error_missing_port, Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, DescriptionActivityWebLocal.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void goToForum(){
		Intent intent = new Intent(this, ForumActivity.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void setConnectionMode(boolean online,CharSequence title){
		_menu.findItem(R.id.map_online).setEnabled(!online);
		_menu.findItem(R.id.map_offline).setEnabled(online);
		_menu.findItem(R.id.menu_connexion).setTitle(title + MyActivity.ARROW);
		if(online){
			this.mode = R.id.map_online;
		}
		else{
			this.mode = R.id.map_offline;
		}
	}

	private void goToAccount(){
		Intent intent = new Intent(this, AccountActivity.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, MyActivity.RESULT_IS_LOGIN);
	}

	private void goToLogin(){
		// Create an instance of the dialog fragment and show it
		LoginDialog dialog = new LoginDialog();
		dialog.show(getFragmentManager(), "LoginDialog");
	}
}
