package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.os.AsyncTask;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;

public class GetPoiInfoAsyncTask extends AsyncTask<String,Void,String>
{	
	OverlayItem item;
	public GetPoiInfoAsyncTask(OverlayItem item){
		this.item = item;
	}
	
	@Override
	protected String doInBackground(String... params)
	{
		PoiController poiController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return "";
			}
			else{
				String id = (String) params[0];
				CommentairesDTOList commentaires = poiController.listeDesCommentairesPourUnPoint(id);
				
				//get contenu
				String contenu = commentaires.get(commentaires.size()-1).getContenu();
				return contenu;
			}
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return "non charger";
	}
	
	@Override
	protected void onPostExecute (String contenu) {
		item.setSnippet(contenu);
	}

}