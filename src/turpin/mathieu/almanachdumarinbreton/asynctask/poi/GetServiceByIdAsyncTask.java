package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;

public class GetServiceByIdAsyncTask extends MyAsyncTask{

	public interface GetServiceByIdListener {
		void setPoi(ServiceDTO poi,int index);
	}
	
	private String title;
	private int index;
	Map<String,String> params;

	public GetServiceByIdAsyncTask(Context context, String title,int index) {
		super(context, title);
		this.index = index;
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
				ServiceDTO service = poiController.findServiceById(params[0]);
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		ServiceDTO poi = (ServiceDTO) object;
		if(poi != null){
			GetServiceByIdListener listener = (GetServiceByIdListener) context;
			listener.setPoi(poi,index);
		}
		else{
			new GetPoiByIdAsyncTask(context,title,index).execute(params);
		}
	}
}
