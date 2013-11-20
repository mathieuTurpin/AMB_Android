package turpin.mathieu.almanachdumarinbreton;

import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.core.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
	private final MainActivity myMapViewer;
	private boolean centerAtFirstFix;
	private Location positionMarina;
	
	public MyLocationListener(MainActivity myMapViewer) {
		this.myMapViewer = myMapViewer;
		this.positionMarina = new Location("");
		this.positionMarina.setLatitude(48.3794);
		this.positionMarina.setLongitude(-4.4926);
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
		
		//Update the orientation
		//if (this.centerAtFirstFix) this.myMapViewer.mySensorListener.registerSensor();
		
		//Center the map on myPosition
		if (this.centerAtFirstFix || this.myMapViewer.isSnapToLocationEnabled()) {
			this.centerAtFirstFix = false;
			this.myMapViewer.mapView.getController().setCenter(point);
			if(location.hasSpeed() && location.getSpeed()!=0.0){
				this.myMapViewer.infoSpeed.setText(location.getSpeed()+" m/s");
			}
			if(location.hasBearing() && location.getBearing()!=0.0){
				this.myMapViewer.infoBearing.setText(location.getBearing()+ "°");
				Drawable c = rotateDrawable(location.getBearing());
                myMapViewer.overlayItem.setMarker(ItemizedOverlay.boundCenter(c));
        		myMapViewer.itemizedOverlay.requestRedraw();
			}
			this.myMapViewer.infoPosition.setText("lat: "+location.getLatitude()+"°, lon: "+location.getLongitude()+"°");
			
			//Echelle en fonction de la distance
			if(location.distanceTo(positionMarina) < 250){
				if(this.myMapViewer.mapView.getMapPosition().getMapPosition().zoomLevel != 18){
					this.myMapViewer.mapView.getController().setZoom(18);
				}
			}
			else if(location.distanceTo(positionMarina) < 500){
				if(this.myMapViewer.mapView.getMapPosition().getMapPosition().zoomLevel != 17){
					this.myMapViewer.mapView.getController().setZoom(17);
				}
			}
			else if(location.distanceTo(positionMarina) < 1000){
				if(this.myMapViewer.mapView.getMapPosition().getMapPosition().zoomLevel != 16){
					this.myMapViewer.mapView.getController().setZoom(16);
				}
			}
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
	
	public BitmapDrawable rotateDrawable(float angle)
	{
	  Bitmap arrowBitmap = BitmapFactory.decodeResource(this.myMapViewer.getResources(), R.drawable.bateau);
	 
	  // Create blank bitmap of equal size
	  Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
	  canvasBitmap.eraseColor(0x00000000);

	  // Create canvas
	  Canvas canvas = new Canvas(canvasBitmap);

	  // Create rotation matrix
	  Matrix rotateMatrix = new Matrix();
	  rotateMatrix.setRotate(angle, canvas.getWidth()/2, canvas.getHeight()/2);

	  // Draw bitmap onto canvas using matrix
	  canvas.drawBitmap(arrowBitmap, rotateMatrix, null);

	  return new BitmapDrawable(canvasBitmap); 
	}
}
