package turpin.mathieu.almanachdumarinbreton.description;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.forum.AccountActivity;
import turpin.mathieu.almanachdumarinbreton.forum.AccountManager;
import turpin.mathieu.almanachdumarinbreton.forum.LoginDialog;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class DescriptionActivity extends Activity{

	//Extra
	final int RESULT_IS_LOGIN = 0;
	final String EXTRA_PORT = "port_name";
	final String EXTRA_MODE_MAP = "mode_map";
	final String EXTRA_URL = "url";
	final String EXTRA_COURT_PORT = "port_court_name";
	final String EXTRA_MODE_DESCRIPTION = "mode_description";

	protected String courtNamePort ="";

	private Menu _menu;

	private AccountManager accountManager;
	
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
			
			int modeMap = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_offline);
			if(modeMap == R.id.map_online){
				_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_online);
				_menu.findItem(R.id.map_online).setEnabled(false);
				_menu.findItem(R.id.map_offline).setEnabled(true);
			}

			int modeDescription = intent.getIntExtra(EXTRA_MODE_DESCRIPTION, R.id.menu_details);
			if(modeDescription != R.id.menu_details){
				MenuItem item = _menu.findItem(modeDescription);
				_menu.findItem(R.id.menu_affichage).setTitle(item.getTitle());
				_menu.findItem(R.id.menu_details).setEnabled(true);
				item.setEnabled(false);
			}
			if(modeDescription == R.id.menu_meteo){
				_menu.findItem(R.id.menu_connexion).setEnabled(false);
			}
			
			accountManager = new AccountManager(getApplicationContext());

			if(accountManager.isLoggedIn()){
				_menu.findItem(R.id.menu_compte).setTitle(getResources().getString(R.string.menu_compte));
			}
			
		}

		return true;
	}
	
	private void goToActivity(Intent intent){
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			intent.putExtra(EXTRA_MODE_MAP, R.id.map_online);
		}
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}

	private void goToMap(){
		Intent intent = new Intent(DescriptionActivity.this, MainActivity.class);
		goToActivity(intent);
	}

	private void goToWebDescription(int id_mode_description,String url){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityWeb.class);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_URL, url);
		goToActivity(intent);
	}

	private void goToWebLocalDescription(int id_mode_description){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityWebLocal.class);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		goToActivity(intent);
	}

	/*
	private void goToTextDescription(int id_mode_description){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityText.class);
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		startActivity(intent);
	}*/

	private void goToImageDescription(int id_mode_description){
		Intent intent = new Intent(DescriptionActivity.this, DescriptionActivityImage.class);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		goToActivity(intent);
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
		case R.id.menu_details:
			// Button behavior "Details"
			goToWebLocalDescription(R.id.menu_details);
			return true;
		case R.id.menu_courant:
			// Button behavior "Courant"
			goToImageDescription(R.id.menu_courant);
			return true;
		case R.id.menu_marees:
			// Button behavior "Marees"
			String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
			if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
				goToWebDescription(R.id.menu_marees,getString(R.string.url_marees));
			}
			else{
				String nameFile = "maree.pdf";
				if(copyReadPdfAssets(nameFile)){
					Intent intent = new Intent(Intent.ACTION_VIEW);
			        intent.setDataAndType(
			                Uri.parse("file://" + getFilesDir() + "/"+nameFile),
			                "application/pdf");
			        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			        try {
			                startActivity(intent);
			                return true;
			        } catch (ActivityNotFoundException e) {
						Toast.makeText(this, "Erreur: vous n'avez pas d'application pour lire un pdf", Toast.LENGTH_SHORT).show();
			        }
				}
				else{
					Toast.makeText(this, "Erreur: Fichier pdf non disponible", Toast.LENGTH_SHORT).show();
				}
			}
			
			return true;
		case R.id.menu_meteo:
			// Button behavior "Meteo"
			String connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
			if(connexion.equals(getResources().getString(R.string.menu_online))){
				goToWebDescription(R.id.menu_meteo,getString(R.string.url_meteo));
			}
			else{
				Toast.makeText(this, "Non disponible en mode offline", Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.menu_compte:
			// Button behavior "Compte"
			if(accountManager.isLoggedIn()){
				String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
				Intent intent = new Intent(DescriptionActivity.this, AccountActivity.class);
				intent.putExtra(EXTRA_PORT, namePort);
				intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);

				mode_connexion = _menu. findItem(R.id.menu_connexion).getTitle().toString();
				if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
					intent.putExtra(EXTRA_MODE_MAP, R.id.map_online);
				}
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
	
	private boolean copyReadPdfAssets(String nameFile)
    {
        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), nameFile);
        try
        {
            in = getAssets().open(nameFile);
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }
        return false;
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
}
