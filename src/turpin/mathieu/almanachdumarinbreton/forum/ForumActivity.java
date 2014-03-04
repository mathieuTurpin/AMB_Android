package turpin.mathieu.almanachdumarinbreton.forum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentaireDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;
import turpin.mathieu.almanachdumarinbreton.MainActivity;
import turpin.mathieu.almanachdumarinbreton.MyActivity;
import turpin.mathieu.almanachdumarinbreton.MyXmlParser;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.ChargementCommentairesAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.ChargementCommentairesAsyncTask.ChargementCommentairesListener;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.GetPoiByIdAsyncTask.GetPoiByIdListener;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.GetServiceByIdAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.GetServiceByIdAsyncTask.GetServiceByIdListener;
import turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur.GetUserByIdAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur.GetUserByIdAsyncTask.GetUserByIdListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ForumActivity extends MyActivity implements ChargementCommentairesListener,GetUserByIdListener,GetServiceByIdListener,GetPoiByIdListener{

	public static final String EXTRA_ID_CENTRE = "id_centre";
	private int idCentreInteret;
	private double latitudeCentreInteret;
	private double longitudeCentreInteret;
	private String nomCentreInteret;
	private MyAccountManager accountManager;

	//-----------------------------------------------------------------------------
	private static final String TAG_NOM = "nom_commentaire";
	private static final String TAG_ID_USER = "id_user";
	private static final String TAG_DATE = "date";
	private static final String TAG_AVATAR = "avatar";
	private static final String TAG_IMG_TYPE_COMMENT = "img_type_comment";
	//-----------------------------------------------------------------------------

	private ListView lv;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forum);

		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null) {
			this.idCentreInteret = savedInstanceState.getInt(EXTRA_ID_CENTRE, -1);
			String idPoi = Integer.toString(idCentreInteret);

			Map<String,String> params = new HashMap<String, String>();
			params.put(ChargementCommentairesAsyncTask.KEY_ID_POI, idPoi);

			new ChargementCommentairesAsyncTask(ForumActivity.this,"Chargement de la liste des commentaires",false).execute(params);
		}
		else{
			initIntentForActivity(intent);
		}

		lv = (ListView) findViewById(android.R.id.list);

		accountManager = new MyAccountManager(this);
		
		Button addComment = (Button) findViewById(R.id.boutonAdd);
		addComment.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(idCentreInteret == -1){
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
				else{
					if(accountManager.isLoggedIn()){
						AddCommentDialog dialog = AddCommentDialog.getInstance(accountManager.getId(),idCentreInteret,latitudeCentreInteret,longitudeCentreInteret,nomCentreInteret);
						dialog.show(getFragmentManager(), "AddCommentDialog");
					}
					else{
						Toast.makeText(ForumActivity.this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
					}
				}
				
			}
		});
		
		String idPoi = Integer.toString(idCentreInteret);

		Map<String,String> params = new HashMap<String, String>();
		params.put(ChargementCommentairesAsyncTask.KEY_ID_POI, idPoi);

		new ChargementCommentairesAsyncTask(ForumActivity.this,"Chargement de la liste des commentaires",false).execute(params);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initIntentForActivity(Intent intent){
		super.initIntentForActivity(intent);
		if (intent != null) {
			this.idCentreInteret = intent.getIntExtra(EXTRA_ID_CENTRE, -1);
		}
		String idPoi = Integer.toString(idCentreInteret);

		Map<String,String> params = new HashMap<String, String>();
		params.put(ChargementCommentairesAsyncTask.KEY_ID_POI, idPoi);

		new ChargementCommentairesAsyncTask(ForumActivity.this,"Chargement de la liste des commentaires",false).execute(params);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.forum, menu);

		//Add an arrow
		String menuMode = getResources().getString(R.string.menu_forum);
		menu.findItem(R.id.menu_mode).setTitle(menuMode + MyActivity.ARROW);

		String menuComment = getResources().getString(R.string.menu_all_comment);
		menu.findItem(R.id.menu_affichage).setTitle(menuComment + MyActivity.ARROW);

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
			startChargementCommentaires(false,R.string.menu_all_comment);
			return true;
		case R.id.menu_my_comment:
			startChargementCommentaires(true,R.string.menu_my_comment);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("unchecked")
	public void startChargementCommentaires(boolean myComment,int id){
		String nameMenu = getResources().getString(id);
		_menu.findItem(R.id.menu_affichage).setTitle(nameMenu + MyActivity.ARROW);
		_menu.findItem(R.id.menu_all_comment).setEnabled(myComment);
		_menu.findItem(R.id.menu_my_comment).setEnabled(!myComment);

		String idPoi = Integer.toString(idCentreInteret);
		String idUtilisateur = Integer.toString(accountManager.getId());

		Map<String,String> params = new HashMap<String, String>();
		params.put(ChargementCommentairesAsyncTask.KEY_ID_POI, idPoi);
		params.put(ChargementCommentairesAsyncTask.KEY_ID_USER, idUtilisateur);
		
		//if(myComment) idCentreInteret = -1;

		new ChargementCommentairesAsyncTask(ForumActivity.this,"Chargement de la liste des commentaires",myComment).execute(params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void chargementCommentaires(CommentairesDTOList commentaires) {
		if(commentaires!=null){
			ArrayList<HashMap<String, String>> listeAffichageCommentaires = new ArrayList<HashMap<String,String>>();
			for(int index = 0; index < commentaires.size(); index++)
			{
				CommentaireDTO commentaire = commentaires.get(index);
				HashMap<String,String> commentaireAffichage = new HashMap<String, String>();

				Map<String,String> params = new HashMap<String, String>();
				Integer idUser = commentaire.getUtilisateurId();
				params.put(UtilisateurController.KEY_ID, Integer.toString(idUser));

				new GetUserByIdAsyncTask(ForumActivity.this,"Recherche info utilisateur",index).execute(params);

				params = new HashMap<String, String>();
				Integer idPoi = commentaire.getPointId();
				params.put(PoiController.KEY_ID, Integer.toString(idPoi));
				new GetServiceByIdAsyncTask(ForumActivity.this,"Recherche info poi",index).execute(params);
				
				commentaireAffichage.put(TAG_AVATAR, Integer.toString(R.drawable.ic_action_person));
				commentaireAffichage.put(TAG_NOM, commentaire.getContenu());
				commentaireAffichage.put(TAG_DATE, commentaire.getDatePublication().toLocaleString());

				listeAffichageCommentaires.add(commentaireAffichage);
			}
			ListAdapter adapter = new SimpleAdapter(ForumActivity.this,
					listeAffichageCommentaires,
					R.layout.list_item_commentaire,
					new String[] {TAG_AVATAR,TAG_ID_USER,TAG_NOM,TAG_DATE,TAG_IMG_TYPE_COMMENT},
					new int[] { R.id.avatar,R.id.id_user,R.id.nom_commentaire,R.id.date,R.id.type_comment});
					

			lv.setAdapter(adapter);

		}
		else{
			Toast.makeText(ForumActivity.this, "Aucun commentaire", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void setUser(UtilisateurDTO user,int index) {
		@SuppressWarnings("unchecked")
		HashMap<String,String> commentaireAffichage = (HashMap<String, String>) lv.getAdapter().getItem(index);
		commentaireAffichage.put(TAG_ID_USER, user.getNom() + " " + user.getPrenom());
		
		if(user.getUrlAvatar() != null){
			//Donwload the avatar and add update commentaireAffichage
			
		}
		
		((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
	}

	private void setTypeComment(String type, int index,double latitude, double longitude,String nomCentreInteret){

		int idDrawable = MyXmlParser.getInstance().getDrawablePoiByType(type);

		if(idDrawable != -1){
			@SuppressWarnings("unchecked")
			HashMap<String,String> commentaireAffichage = (HashMap<String, String>) lv.getAdapter().getItem(index);
			commentaireAffichage.put(TAG_IMG_TYPE_COMMENT, Integer.toString(idDrawable));

			((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
		}
		
		this.latitudeCentreInteret = latitude;
		this.longitudeCentreInteret = longitude;
		this.nomCentreInteret = nomCentreInteret;
	}

	@Override
	public void setPoi(PoiDTO poi,int index) {
		setTypeComment(poi.getType(),index, Double.parseDouble(poi.getLatitude()),Double.parseDouble(poi.getLongitude()),poi.getType());
	}

	@Override
	public void setPoi(ServiceDTO poi,int index) {
		setTypeComment(poi.getType(),index, Double.parseDouble(poi.getLatitude()),Double.parseDouble(poi.getLongitude()),poi.getDescription());
	}
}