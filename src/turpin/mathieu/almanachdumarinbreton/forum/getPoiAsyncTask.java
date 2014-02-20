package turpin.mathieu.almanachdumarinbreton.forum;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.mapsforge.android.maps.MyMapView;

import android.os.AsyncTask;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoisDTOList;

public class getPoiAsyncTask extends AsyncTask<MyMapView, Void, PoisDTOList>
{
	private MyMapView mapView;
	@Override
	protected PoisDTOList doInBackground(MyMapView... params)
	{
		mapView = params[0];
		PoiController poiController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				PoisDTOList poi = poiController.findAllPoiJson();
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
	protected void onPostExecute (PoisDTOList poi) {
		if(poi!=null){
			mapView.setPoi(poi);
		}
		else{
		}
	}

}
