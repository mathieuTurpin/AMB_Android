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

	private String courtNamePort ="";

	private Menu _menu;

	private EditText pseudoEdit;
	private RadioGroup radioGroup;

	private AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		Intent intent = getIntent();
		if (intent != null) {
			String courtName = intent.getStringExtra(EXTRA_COURT_PORT);
			if(courtName != null){
				this.courtNamePort = courtName;
			}
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

		Intent intent = getIntent();
		if (intent != null) {
			String menuPort = intent.getStringExtra(EXTRA_PORT);
			if(menuPort != null){
				_menu.findItem(R.id.menu_port).setTitle(menuPort);
			}

			int modeMap = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_offline);
			if(modeMap == R.id.map_online){
				_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_online);
				_menu.findItem(R.id.map_online).setEnabled(false);
				_menu.findItem(R.id.map_offline).setEnabled(true);
			}
		}

		return true;
	}

	private void goToMap(){
		Intent intent = new Intent(AccountActivity.this, MainActivity.class);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			intent.putExtra(EXTRA_MODE_MAP, R.id.map_online);
		}
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_marina:
			// Button behavior "Marina"
			return true;
		case R.id.map:
			goToMap();
			return true;
		case R.id.map_description:
			// Button behavior "Map Decription"
			// if no port is selected
			String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
			if(namePort.equals(getResources().getString(R.string.menu_port))){
				Toast.makeText(AccountActivity.this, R.string.error_missing_port, Toast.LENGTH_SHORT).show();
				return true;
			}
			Intent intent = new Intent(AccountActivity.this, DescriptionActivityWebLocal.class);
			intent.putExtra(EXTRA_PORT, namePort);
			intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);

			String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
			if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
				intent.putExtra(EXTRA_MODE_MAP, R.id.map_online);
			}
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
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
