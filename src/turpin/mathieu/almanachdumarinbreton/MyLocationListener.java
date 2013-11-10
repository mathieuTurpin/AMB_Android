package turpin.mathieu.almanachdumarinbreton;

import org.mapsforge.core.GeoPoint;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
	private final MainActivity myMapViewer;
	private boolean centerAtFirstFix;

	public MyLocationListener(MainActivity myMapViewer) {
		this.myMapViewer = myMapViewer;
	}

	@Override
	public void onLocationChanged(Location location) {
		//Get myPosition
		GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
		
		//Update myPosition on the map
		this.myMapViewer.overlayCircle.setCircleData(point, location.getAccuracy());
		this.myMapViewer.overlayItem.setPoint(point);
		this.myMapViewer.circleOverlay.requestRedraw();
		this.myMapViewer.itemizedOverlay.requestRedraw();
		
		//Center the map on myPosition
		if(this.centerAtFirstFix){
			this.myMapViewer.mapController.setCenter(point);
			this.centerAtFirstFix = false;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// do nothing
	}

	@Override
	public void onProviderEnabled(String provider) {
		// do nothing
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// do nothing
	}

	boolean isCenterAtFirstFix() {
		return this.centerAtFirstFix;
	}

	void setCenterAtFirstFix(boolean centerAtFirstFix) {
		this.centerAtFirstFix = centerAtFirstFix;
	}
}
