package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;

public abstract class GetIdPoiAsyncTask extends MyAsyncTask
{
	//A CONTINUER
	public GetIdPoiAsyncTask(Context context, String title) {
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
				PoiDTO poi = poiController.findPoiByPosition(params[0]);
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
}
