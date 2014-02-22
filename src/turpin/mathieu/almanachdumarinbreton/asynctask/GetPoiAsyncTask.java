package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.os.AsyncTask;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoisDTOList;

public class GetPoiAsyncTask extends AsyncTask<Void, Void, PoisDTOList>
{
	public interface GetPoiListener {
		void setPoi(PoisDTOList poi);
	}

	private GetPoiListener listener;

	public GetPoiAsyncTask(GetPoiListener listener){
		super();
		this.listener = listener;
	}

	@Override
	protected PoisDTOList doInBackground(Void...params)
	{
		PoiController poiController = PoiController.getInstance();
		try
		{
			PoisDTOList poi= poiController.findAllPoiJson();
			return poi;
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
	protected void onPostExecute (PoisDTOList poi) {
		if(poi!=null){
			listener.setPoi(poi);
		}
	}
}
