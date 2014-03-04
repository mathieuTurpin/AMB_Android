package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import java.io.IOException;
import java.util.Map;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;

public class ChargementCommentairesAsyncTask extends MyAsyncTask
{
	public interface ChargementCommentairesListener{
		void chargementCommentaires(CommentairesDTOList commentaires);
	}
	
	public static final String KEY_ID_POI = "id_poi";
	public static final String KEY_ID_USER = "id_user";
	private final boolean onlyMyComment;
	
	public ChargementCommentairesAsyncTask(Context context, String title,boolean onlyMyComment) {
		super(context,title);
		this.onlyMyComment = onlyMyComment;
	}

	protected CommentairesDTOList doInBackground(Map<String, String>... params)
	{
		PoiController poiController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				String idPoi = params[0].get(KEY_ID_POI);
				if(idPoi.equals("-1")){
					if(onlyMyComment){
						String idUtilisateur = params[0].get(KEY_ID_USER);
						return poiController.listeDesCommentairesPourUnUtilisateur(idUtilisateur);
					}
					else{
						return poiController.findAllCommentairesJson();
					}
				}
				else{
					return poiController.listeDesCommentairesPourUnPoint(idPoi);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Object object)
	{
		super.onPostExecute(object);
		if(object != null){
			CommentairesDTOList commentaires = (CommentairesDTOList) object;

			ChargementCommentairesListener listener = (ChargementCommentairesListener) context;
			listener.chargementCommentaires(commentaires);
		}
		else{
			Toast.makeText(context, "Erreur lors du chargement des commentaires", Toast.LENGTH_SHORT).show();
		}
	}
}