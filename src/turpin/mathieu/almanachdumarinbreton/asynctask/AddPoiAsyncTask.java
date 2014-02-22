package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;

public class AddPoiAsyncTask extends MyAsyncTask
{
	public interface AddPoiListener {
		void addPoi(PoiDTO poi,String contenu);
	}

	private String contenu;

	public AddPoiAsyncTask(Context context, String title) {
		super(context, title);
	}

	@Override
	protected PoiDTO doInBackground(Map<String,String>... params)
	{
		PoiController poiController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				contenu = params[0].get(PoiController.KEY_CONTENU);
				PoiDTO poi = poiController.addPoi(params[0]);
				return poi;
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

	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		PoiDTO poi = (PoiDTO) object;
		if(poi!=null){
			AddPoiListener listener = (AddPoiListener) context;
			listener.addPoi(poi,contenu);
			Toast.makeText(context, "Enregistrement terminé", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
		}
	}
}