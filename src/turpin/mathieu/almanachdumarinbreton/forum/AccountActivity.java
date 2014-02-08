package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateursDTOList;
import turpin.mathieu.almanachdumarinbreton.MyActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AccountActivity extends MyActivity{
	//-----------------------------------------------------------------------------
	private static final String TAG_NOM_COMPLET = "nom_complet_utilisateur";
	//-----------------------------------------------------------------------------

	private EditText nomEdit;
	private EditText prenomEdit;
	private EditText descriptionEdit;
	private RadioGroup radioGroup;

	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		lv = (ListView) findViewById(android.R.id.list);

		String myName = accountManager.getName();
		nomEdit = (EditText) findViewById(R.id.nom);
		nomEdit.setText(myName);

		String myFirstName = accountManager.getFirstName();
		prenomEdit = (EditText) findViewById(R.id.prenom);
		prenomEdit.setText(myFirstName);

		String myDescription = accountManager.getDescription();
		descriptionEdit = (EditText) findViewById(R.id.description);
		descriptionEdit.setText(myDescription);

		radioGroup = (RadioGroup) findViewById(R.id.radioPartageGroup); 
		int sharedMode = accountManager.getPartage();
		getShareButtonByMode(sharedMode).setChecked(true);

		Button btnSave = (Button) findViewById(R.id.boutonSave);
		btnSave.setOnClickListener(new OnClickListener()
		{
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v)
			{
				String newName = nomEdit.getText().toString();

				String newFirstName = prenomEdit.getText().toString();

				String newDescription = descriptionEdit.getText().toString();

				int idButtonChecked = radioGroup.getCheckedRadioButtonId();
				int sharedMode = getSharedModeByButton(idButtonChecked);
				accountManager.setPartage(sharedMode);
				
				Map<String,String> formValues = UtilisateurController.getInstance().prepareUpdate(""+accountManager.getId(),newName,newFirstName, accountManager.getEmail(),newDescription, ""+!accountManager.getNoPartage(), ""+accountManager.getPartagePublic());
				new UpdateUtilisateurAsyncTask().execute(formValues);
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

		new ChargementUtilisateursAsyncTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.account, menu);

		return super.onCreateOptionsMenu(menu);
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

	//-----------------------------------------------------------------------------
	// extends AsyncTask<Params, Progress, Result>
	protected class ChargementUtilisateursAsyncTask extends AsyncTask<Void, Integer, UtilisateursDTOList>
	{
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog = new ProgressDialog(AccountActivity.this);
			progressDialog.setTitle("Chargement de la liste des utilisateurs");
			progressDialog.setMessage("En cours...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		@Override
		// doInBackground(Params... params)
		protected UtilisateursDTOList doInBackground(Void... formValues)
		{
			UtilisateurController utilisateurController = UtilisateurController.getInstance();
			try
			{
				Log.d("AMBSocialNetwork", "-----------------------------> " + utilisateurController.findAllJson().getClass().getName());
				return utilisateurController.findAllJson();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		@Override
		// onPostExecute(Result response)
		protected void onPostExecute(UtilisateursDTOList utilisateurs)
		{
			progressDialog.dismiss();

			ArrayList<HashMap<String, String>> listeAffichageUtilisateurs = new ArrayList<HashMap<String,String>>();
			for(UtilisateurDTO utilisateur : utilisateurs)
			{
				HashMap<String,String> utilisateurAffichage = new HashMap<String, String>();
				utilisateurAffichage.put(TAG_NOM_COMPLET, utilisateur.getNom() + " " + utilisateur.getPrenom());
				listeAffichageUtilisateurs.add(utilisateurAffichage);
			}

			ListAdapter adapter = new SimpleAdapter(AccountActivity.this, 
					listeAffichageUtilisateurs,
					R.layout.list_item_utilisateur,
					new String[] {TAG_NOM_COMPLET}, 
					new int[] { R.id.nom_complet_utilisateur});

			lv.setAdapter(adapter);
		}
	}
	//-----------------------------------------------------------------------------
	

	protected class UpdateUtilisateurAsyncTask extends AsyncTask<Map<String,String>, Void, UtilisateurDTO>{
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog = new ProgressDialog(AccountActivity.this);
			progressDialog.setTitle("Enregistrement");
			progressDialog.setMessage("En cours...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		// doInBackground(Params... params)
		protected UtilisateurDTO doInBackground(Map<String,String>... formValues)
		{
			UtilisateurController utilisateurController = UtilisateurController.getInstance();
			try
			{
				if(formValues.length < 1){
					return null;
				}
				else{			
					return utilisateurController.update(formValues[0]);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute (UtilisateurDTO user) {
			progressDialog.dismiss();
			if(user!=null){
				//Save new parameters
				accountManager.logIn(user);
				
				Toast.makeText(AccountActivity.this, "Enregistrement terminé", Toast.LENGTH_SHORT).show();
				//AccountActivity.this.setResult(Activity.RESULT_OK);
				//AccountActivity.this.finish();
			}
			else{
				Toast.makeText(AccountActivity.this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
