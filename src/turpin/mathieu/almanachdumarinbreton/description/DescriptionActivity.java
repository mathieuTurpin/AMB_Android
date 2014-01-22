package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class DescriptionActivity extends Activity{

	//Extra
	final String EXTRA_PORT = "port_name";
	final String EXTRA_MODE_MAP = "mode_map";
	final String EXTRA_URL = "url";
	final String EXTRA_COURT_PORT = "port_court_name";
	final String EXTRA_MODE_DESCRIPTION = "mode_description";

	protected String courtNamePort ="";

	private Menu _menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent != null) {
			String courtName = intent.getStringExtra(EXTRA_COURT_PORT);
			if(courtName != null){
				this.courtNamePort = courtName;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.description, menu);
		_menu = menu;

		Intent intent = getIntent();
		if (intent != null) {
			String menuPort = intent.getStringExtra(EXTRA_PORT);
			if(menuPort != null){
				_menu.findItem(R.id.menu_port).setTitle(menuPort);
			}

			int modeDescription = intent.getIntExtra(EXTRA_MODE_DESCRIPTION, R.id.menu_details);
			if(modeDescription != R.id.menu_details){
				MenuItem item = _menu.findItem(modeDescription);
				_menu.findItem(R.id.menu_affichage).setTitle(item.getTitle());
				_menu.findItem(R.id.menu_details).setEnabled(true);
				item.setEnabled(false);
			}
		}

		return true;
	}

	private void goToMap(String mode_map){
		Intent intent = new Intent(DescriptionActivity.this, MainActivity.class);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_MODE_MAP, mode_map);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	private void goToWebDescription(int id_mode_description,String url){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityWeb.class);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_URL, url);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	private void goToWebLocalDescription(int id_mode_description,String url){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityWebLocal.class);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_URL, url);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	private void goToTextDescription(int id_mode_description){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityText.class);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	private void goToImageDescription(int id_mode_description){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityImage.class);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_marina:
			// Button behavior "Marina"
			return true;
		case R.id.map_offline:
			// Button behavior "Map offline"
			goToMap("mode_offline");
			return true;
		case R.id.map_online:
			// Button behavior "Map Online"
			goToMap("mode_online");
			return true;
		case R.id.menu_details:
			// Button behavior "Details"
			goToTextDescription(R.id.menu_details);
			return true;
		case R.id.menu_courant:
			// Button behavior "Courant"
			goToImageDescription(R.id.menu_courant);
			return true;
		case R.id.menu_marees:
			// Button behavior "Marees"
			goToWebDescription(R.id.menu_marees,getString(R.string.url_marees));
			return true;
		case R.id.menu_meteo:
			// Button behavior "Meteo"
			goToWebDescription(R.id.menu_meteo,getString(R.string.url_meteo));
			return true;
		case R.id.menu_compte:
			// Button behavior "Compte"
			Toast.makeText(this, "Compte", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
