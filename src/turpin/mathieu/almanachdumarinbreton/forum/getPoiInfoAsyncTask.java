package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.os.AsyncTask;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;

public class getPoiInfoAsyncTask extends AsyncTask<OverlayItem, Void, Void>
{
	@Override
	protected Void doInBackground(OverlayItem... params)
	{
		OverlayItem item = params[0];
		PoiController poiController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				//get id
				CommentairesDTOList commentaires = poiController.listeDesCommentairesPourUnPoint(item.getSnippet());
				//get contenu
				String contenu = commentaires.get(0).getContenu();
				item.setSnippet(contenu);
				return null;
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
		return null;
	}

}
