package turpin.mathieu.almanachdumarinbreton.description;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import turpin.mathieu.almanachdumarinbreton.MyActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class DescriptionActivity extends MyActivity{

	protected final String EXTRA_URL = "url";
	protected final String EXTRA_MODE_DESCRIPTION = "mode_description";

	protected int modeDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null) {
			this.modeDescription = savedInstanceState.getInt(EXTRA_MODE_DESCRIPTION, R.id.menu_details);
		}
		else{
			initIntentForActivity(intent);
		}
	}

	@Override
	protected void initIntentForActivity(Intent intent){
		super.initIntentForActivity(intent);
		if (intent != null) {
			this.modeDescription = intent.getIntExtra(EXTRA_MODE_DESCRIPTION, R.id.menu_details);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.description, menu);

		//Add an arrow
		String menuMode = getResources().getString(R.string.menu_description);
		menu.findItem(R.id.menu_mode).setTitle(menuMode + MyActivity.ARROW);

		return super.onCreateOptionsMenu(menu);
	}

	protected void initMenu(){
		super.initMenu();

		MenuItem item = _menu.findItem(modeDescription);
		_menu.findItem(R.id.menu_affichage).setTitle(item.getTitle() + MyActivity.ARROW);

		if(modeDescription != R.id.menu_details){
			_menu.findItem(R.id.menu_details).setEnabled(true);
			item.setEnabled(false);
		}
		if(modeDescription == R.id.menu_meteo){
			_menu.findItem(R.id.menu_connexion).setEnabled(false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(EXTRA_MODE_DESCRIPTION, this.modeDescription);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_details:
			goToWebLocalDescription(R.id.menu_details);
			return true;
		case R.id.menu_courant:
			goToImageDescription(R.id.menu_courant);
			return true;
		case R.id.menu_marees:
			Intent i;
	        try {
	            i = getPackageManager().getLaunchIntentForPackage("fr.aperto.android.tides");
	            if (i == null)
	                throw new PackageManager.NameNotFoundException();
	            i.addCategory(Intent.CATEGORY_LAUNCHER);
	            startActivity(i);
	        } catch (PackageManager.NameNotFoundException e) {
	        	//String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
	        	//if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
				if(this.mode == R.id.map_online){
	        		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=fr.aperto.android.tides" /*+ appPackageName)*/)));	
	        	}
				else{
					String nameFile = "mareeNew.pdf";
					displayPdf(nameFile);
				}
		}
		return true;
			/*String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
			if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
				goToWebDescription(R.id.menu_marees,getString(R.string.url_marees));
			}
			else{
				String nameFile = "mareeNew.pdf";
				displayPdf(nameFile);
			}
			return true;*/
		case R.id.menu_meteo:
			//String connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
			//if(connexion.equals(getResources().getString(R.string.menu_online))){
			if(this.mode == R.id.map_online){
				goToWebDescription(R.id.menu_meteo,getString(R.string.url_meteo));
			}
			else{
				Toast.makeText(this, "Non disponible en mode offline", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void goToWebDescription(int id_mode_description,String url){
		Intent intent = new Intent(this, DescriptionActivityWeb.class);
		initIntent(intent);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.putExtra(EXTRA_URL, url);
		startActivity(intent);
	}

	private void goToWebLocalDescription(int id_mode_description){
		Intent intent = new Intent(this, DescriptionActivityWebLocal.class);
		initIntent(intent);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void goToImageDescription(int id_mode_description){
		Intent intent = new Intent(this, DescriptionActivityStream.class);
		initIntent(intent);
		intent.putExtra(EXTRA_MODE_DESCRIPTION, id_mode_description);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void displayPdf(String nameFile){
		if(copyReadPdfAssets(nameFile)){
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.parse("file://" + getFilesDir() + "/"+nameFile),
					"application/pdf");
			try {
				startActivity(intent);
				return;
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Erreur: vous n'avez pas d'application pour lire un pdf", Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Toast.makeText(this, "Erreur: Fichier pdf non disponible", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("WorldReadableFiles")
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
