package turpin.mathieu.almanachdumarinbreton.asynctask;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import android.content.Context;
import android.widget.Toast;

public class AddCommentPoiAsyncTask extends GetIdPoiAsyncTask{
	
	private AddCommentListener listener;
	
	public AddCommentPoiAsyncTask(Context context, String title,AddCommentListener listener) {
		super(context, title);
		this.listener = listener;
	}

	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		PoiDTO poi = (PoiDTO) object;
		if(poi != null){
			listener.addComment(poi.getId(),poi.getType());
		}
		else{
			Toast.makeText(context, "Erreur lors de la recherche du centre d'interet sur le serveur", Toast.LENGTH_SHORT).show();
		}
	}

}
