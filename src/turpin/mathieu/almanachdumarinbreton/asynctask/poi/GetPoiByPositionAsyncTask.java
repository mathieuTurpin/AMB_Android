package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;

public abstract class GetPoiByPositionAsyncTask extends MyAsyncTask
{
	public GetPoiByPositionAsyncTask(Context context, String title) {
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