package turpin.mathieu.almanachdumarinbreton.asynctask;

import turpin.mathieu.almanachdumarinbreton.overlay.InfoOverlayItemDialog.InfoOverlayItemDialogListener;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import android.content.Context;
import android.widget.Toast;

public class CommentByIdPoiAsyncTask extends GetIdPoiAsyncTask{

	public CommentByIdPoiAsyncTask(Context context, String title) {
		super(context, title);
	}
	
	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		
		PoiDTO poi = (PoiDTO) object;
		if(poi != null){
			int nbComment = poi.getCommentaires().size();
			if(nbComment<1){
				Toast.makeText(context, "Aucun commentaire", Toast.LENGTH_SHORT).show();
			}
			else{
				InfoOverlayItemDialogListener mActivity = (InfoOverlayItemDialogListener) context;
				mActivity.commentByIdCentreInteret(poi.getId().intValue());
			}
		}
		else{
			Toast.makeText(context, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
		}
	}

}
