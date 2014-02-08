package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.CentreInteretController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentaireDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;
import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.MyActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ForumActivity extends MyActivity{

	public static final String EXTRA_ID_CENTRE = "id_centre";
	private int idCentreInteret;
	private boolean onlyMyComment = false;
	private AccountManager accountManager;
	
	//-----------------------------------------------------------------------------
	private static final String TAG_NOM = "nom_commentaire";
	private static final String TAG_ID_USER = "id_user";
	private static final String TAG_DATE = "date";
	//-----------------------------------------------------------------------------
	
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum);
		
		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null) {
			this.idCentreInteret = savedInstanceState.getInt(EXTRA_ID_CENTRE, -1);
		}
		else{
			initIntentForActivity(intent);
		}
		
		lv = (ListView) findViewById(android.R.id.list);

		Button addComment = (Button) findViewById(R.id.boutonAdd);
		addComment.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// Use the Builder class for convenient dialog construction
				AlertDialog.Builder builder = new AlertDialog.Builder(ForumActivity.this);
				// Set the dialog title
				builder.setTitle("Aide");
				builder.setMessage("Faite un clic long sur la carte pour ajouter un commentaire");

				// Add action buttons
				builder.setPositiveButton("Voir la carte", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//Go to map activity
						Intent intent = new Intent(ForumActivity.this, MainActivity.class);
						initIntent(intent);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(intent);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

				builder.create().show();
			}
		});
		accountManager = new AccountManager(this);
		
		new ChargementCommentairesAsyncTask().execute();
	}
	
	@Override
	protected void initIntentForActivity(Intent intent){
		super.initIntentForActivity(intent);
		if (intent != null) {
			this.idCentreInteret = intent.getIntExtra(EXTRA_ID_CENTRE, -1);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.forum, menu);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt(EXTRA_ID_CENTRE, this.idCentreInteret);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_all_comment:
			ChargementCommentaires(false,R.string.menu_all_comment);
			return true;
		case R.id.menu_my_comment:
			ChargementCommentaires(true,R.string.menu_my_comment);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void ChargementCommentaires(boolean myComment,int id){
		_menu.findItem(R.id.menu_affichage).setTitle(getResources().getString(id));
		_menu.findItem(R.id.menu_all_comment).setEnabled(myComment);
		_menu.findItem(R.id.menu_my_comment).setEnabled(!myComment);
		this.onlyMyComment = myComment;
		new ChargementCommentairesAsyncTask().execute();
	}

	//-----------------------------------------------------------------------------
	// extends AsyncTask<Params, Progress, Result>
	protected class ChargementCommentairesAsyncTask extends AsyncTask<Void, Integer, CommentairesDTOList>
	{
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressDialog = new ProgressDialog(ForumActivity.this);
			progressDialog.setTitle("Chargement de la liste des commentaires");
			progressDialog.setMessage("En cours...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		@Override
		// doInBackground(Params... params)
		protected CommentairesDTOList doInBackground(Void... formValues)
		{
			CentreInteretController centreInteretController = CentreInteretController.getInstance();
			try
			{
				if(idCentreInteret == -1){
					if(onlyMyComment){
						String idUtilisateur = Integer.toString(accountManager.getId());
						return centreInteretController.listeDesCommentairesPourUnUtilisateur(idUtilisateur);
					}
					else{
						return centreInteretController.findAllCommentairesJson();
					}
				}
				else{
					return centreInteretController.listeDesCommentairesPourUnCentreInteret(Integer.toString(idCentreInteret));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		@Override
		// onPostExecute(Result response)
		protected void onPostExecute(CommentairesDTOList commentaires)
		{
			progressDialog.dismiss();

			ArrayList<HashMap<String, String>> listeAffichageCommentaires = new ArrayList<HashMap<String,String>>();
			for(CommentaireDTO commentaire : commentaires)
			{
				HashMap<String,String> commentaireAffichage = new HashMap<String, String>();
				commentaireAffichage.put(TAG_ID_USER, ""+commentaire.getUtilisateurId());
				commentaireAffichage.put(TAG_NOM, commentaire.getContenu());
				commentaireAffichage.put(TAG_DATE, commentaire.getDatePublication().toLocaleString());
				listeAffichageCommentaires.add(commentaireAffichage);
			}

			ListAdapter adapter = new SimpleAdapter(ForumActivity.this, 
					listeAffichageCommentaires,
					R.layout.list_item_commentaire,
					new String[] {TAG_ID_USER,TAG_NOM,TAG_DATE}, 
					new int[] { R.id.id_user,R.id.nom_commentaire,R.id.date});

			lv.setAdapter(adapter);
		}
	}
	//-----------------------------------------------------------------------------
}
