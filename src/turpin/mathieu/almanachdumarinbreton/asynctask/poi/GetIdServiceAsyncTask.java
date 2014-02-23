package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;

public abstract class GetIdServiceAsyncTask extends MyAsyncTask{
	
	protected String title;
	Map<String,String> params;
	
	public GetIdServiceAsyncTask(Context context, String title) {
		super(context, title);
		this.title = title;
	}
	
	@Override
	protected ServiceDTO doInBackground(Map<String,String>... params)
	{
		PoiController poiController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				this.params = params[0];
				ServiceDTO service = poiController.findServiceByPosition(params[0]);
				return service;
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
