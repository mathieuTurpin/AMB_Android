package turpin.mathieu.almanachdumarinbreton.asynctask.poi;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;

public class GetPoiByIdAsyncTask extends MyAsyncTask
{
	public interface GetPoiByIdListener {
		void setPoi(PoiDTO poi, int index);
	}
	
	private int index;
	
	public GetPoiByIdAsyncTask(Context context, String title,int index) {
		super(context, title);
		this.index = index;
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
				PoiDTO poi = poiController.findPoiById(params[0]);
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
		if(poi != null){
			GetPoiByIdListener listener = (GetPoiByIdListener) context;
			listener.setPoi(poi,index);
		}
		else{
			
		}
	}
}
