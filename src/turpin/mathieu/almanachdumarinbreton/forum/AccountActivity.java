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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AccountActivity extends Activity{

	//Extra
	final String EXTRA_PORT = "port_name";
	final String EXTRA_COURT_PORT = "port_court_name";
	final String EXTRA_MODE_MAP = "mode_map";

	private int mode;
	private String courtNamePort ="";
	private String port ="";

	private Menu _menu;

	private EditText pseudoEdit;
	private RadioGroup radioGroup;

	private AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

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

		accountManager = new AccountManager(getApplicationContext());

		String myPseudo = accountManager.getPseudo();
		pseudoEdit = (EditText) findViewById(R.id.pseudo);
		pseudoEdit.setText(myPseudo);

		radioGroup = (RadioGroup) findViewById(R.id.radioPartageGroup); 
		int sharedMode = accountManager.getPartage();
		getShareButtonByMode(sharedMode).setChecked(true);

		Button btnSave = (Button) findViewById(R.id.boutonSave);
		btnSave.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String newPseudo = pseudoEdit.getText().toString();
				accountManager.setPseudo(newPseudo);

				int idButtonChecked = radioGroup.getCheckedRadioButtonId();
				int sharedMode = getSharedModeByButton(idButtonChecked);
				accountManager.setPartage(sharedMode);
				setResult(Activity.RESULT_OK);
				finish();
			}
		});

		Button btnLogOut = (Button) findViewById(R.id.boutonLogout);
		btnLogOut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				accountManager.logOut();
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		});
	}

	private int getSharedModeByButton(int id){
		switch(id){
		case R.id.radioNoPartage:
			return AccountManager.NO_PARTAGE;
		case R.id.radioPartagePublic:
			return AccountManager.PARTAGE_PUBLIC;
		case R.id.radioPartagePrivate:
			return AccountManager.PARTAGE_PRIVATE;
		default:
			return AccountManager.NO_PARTAGE;
		}
	}

	private RadioButton getShareButtonByMode(int sharedMode){
		switch(sharedMode){
		case AccountManager.NO_PARTAGE:
			return (RadioButton) findViewById(R.id.radioNoPartage);
		case AccountManager.PARTAGE_PUBLIC:
			return (RadioButton) findViewById(R.id.radioPartagePublic);
		case AccountManager.PARTAGE_PRIVATE:
			return (RadioButton) findViewById(R.id.radioPartagePrivate);
		default:
			return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.account, menu);
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
			intent = new Intent(AccountActivity.this, MainActivity.class);
			initIntent(intent);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.map_description:
			// Button behavior "Map Decription"
			// if no port is selected
			String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
			if(namePort.equals(getResources().getString(R.string.menu_port))){
				Toast.makeText(AccountActivity.this, R.string.error_missing_port, Toast.LENGTH_SHORT).show();
				return true;
			}
			intent = new Intent(AccountActivity.this, DescriptionActivityWebLocal.class);
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
		case R.id.menu_forum:
			intent = new Intent(AccountActivity.this, ForumActivity.class);
			initIntent(intent);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			return true;
		case R.id.menu_compte:
			// Button behavior "Compte"
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
}
