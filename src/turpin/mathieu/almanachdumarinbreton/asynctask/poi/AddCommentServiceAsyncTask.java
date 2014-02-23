package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import android.content.Context;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;

public class AddCommentServiceAsyncTask extends GetIdServiceAsyncTask{

	private AddCommentListener listener;

	public AddCommentServiceAsyncTask(Context context, String title,AddCommentListener listener) {
		super(context, title);
		this.listener = listener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		ServiceDTO poi = (ServiceDTO) object;
		if(poi != null){
			listener.addComment(poi.getId(),poi.getType());
		}
		else{
			new AddCommentPoiAsyncTask(context,title,listener).execute(params);
		}
	}
}
