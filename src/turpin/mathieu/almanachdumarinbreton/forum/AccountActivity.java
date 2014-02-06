package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.UtilisateurDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.UtilisateursList;
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

public class AccountActivity extends MyActivity{
	//-----------------------------------------------------------------------------
	private static final String TAG_ID          = "id_utilisateur";
	private static final String TAG_NOM_COMPLET = "nom_complet_utilisateur";
	private static final String TAG_EMAIL       = "email_utilisateur";
	//-----------------------------------------------------------------------------

	private EditText pseudoEdit;
	private RadioGroup radioGroup;

	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		lv = (ListView) findViewById(android.R.id.list);

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
	protected class ChargementUtilisateursAsyncTask extends AsyncTask<String, Integer, UtilisateursList>
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
		protected UtilisateursList doInBackground(String... params)
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
		protected void onPostExecute(UtilisateursList utilisateurs)
		{
			progressDialog.dismiss();

			ArrayList<HashMap<String, String>> listeAffichageUtilisateurs = new ArrayList<HashMap<String,String>>();
			for(UtilisateurDTO utilisateur : utilisateurs)
			{
				HashMap<String,String> utilisateurAffichage = new HashMap<String, String>();
				utilisateurAffichage.put(TAG_ID,          utilisateur.getId() + "");
				utilisateurAffichage.put(TAG_NOM_COMPLET, utilisateur.getNom() + " " + utilisateur.getPrenom());
				utilisateurAffichage.put(TAG_EMAIL,       utilisateur.getEmail());
				listeAffichageUtilisateurs.add(utilisateurAffichage);
			}

			ListAdapter adapter = new SimpleAdapter(AccountActivity.this, 
					listeAffichageUtilisateurs,
					R.layout.list_item_utilisateur,
					new String[] { TAG_ID, TAG_NOM_COMPLET, TAG_EMAIL}, 
					new int[] { R.id.id_utilisateur, R.id.nom_complet_utilisateur, R.id.email_utilisateur});

			lv.setAdapter(adapter);
		}
	}
	//-----------------------------------------------------------------------------
}
