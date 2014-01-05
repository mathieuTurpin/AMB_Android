package turpin.mathieu.almanachdumarinbreton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class DescriptionActivity extends Activity{
	
	//Extra
	final String EXTRA_PORT = "port_name";
	final String EXTRA_MODE = "mode_map";
	private Menu _menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.description, menu);
		_menu = menu;
		
		Intent intent = getIntent();
		if (intent != null) {
	    	_menu.findItem(R.id.menu_port).setTitle(intent.getStringExtra(EXTRA_PORT));
		}
		
		return true;
	}
	
	private void goToMap(String mode){
		Intent intent = new Intent(DescriptionActivity.this, MainActivity.class);
    	intent.putExtra(EXTRA_MODE, mode);
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
			// Go to mainactivity in mode : offline
        	goToMap("mode_offline");
			return true;
		case R.id.map_online:
			// Button behavior "Map Online"
			// Go to mainactivity in mode : online
			goToMap("mode_online");
			return true;
		case R.id.menu_details:
			// Button behavior "Details"
			Toast.makeText(this, "Description", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_courant:
			// Button behavior "Courant"
			Toast.makeText(this, "Courant", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_marees:
			// Button behavior "Marees"
			Toast.makeText(this, "Marees", Toast.LENGTH_SHORT).show();
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
