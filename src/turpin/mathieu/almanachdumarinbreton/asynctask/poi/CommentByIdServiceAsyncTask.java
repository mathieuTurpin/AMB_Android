package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import turpin.mathieu.almanachdumarinbreton.overlay.InfoOverlayItemDialog.InfoOverlayItemDialogListener;
import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;

public class CommentByIdServiceAsyncTask extends GetIdServiceAsyncTask{
	
	public CommentByIdServiceAsyncTask(Context context, String title) {
		super(context, title);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		ServiceDTO poi = (ServiceDTO) object;
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
			new CommentByIdPoiAsyncTask(context,title).execute(params);
		}
	}
}
