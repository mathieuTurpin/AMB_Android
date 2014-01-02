package turpin.mathieu.almanachdumarinbreton;

import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MapPosition;
import org.mapsforge.core.MercatorProjection;

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
	private final double marinaLatitude = 48.3794;
	private final double marinaLongitude = -4.4926;
	private final Location positionMarina;

	//5 minutes
	private static final int TIME = 300;
	private static final float MILE_TO_METER = Float.parseFloat("1609.344");

	private static final double METER_FOOT_RATIO = 0.3048;
	private static final int BITMAP_WIDTH = 150;

	private static final int[] SCALE_BAR_VALUES_IMPERIAL = { 26400000, 10560000, 5280000, 2640000, 1056000, 528000,
		264000, 105600, 52800, 26400, 10560, 5280, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1 };
	private static final int[] SCALE_BAR_VALUES_METRIC = { 10000000, 5000000, 2000000, 1000000, 500000, 200000, 100000,
		50000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 20, 10, 5, 2, 1 };

	/**
	 * @param context
	 *            the enclosing {@link MainActivity} instance.
	 */
	public MyLocationListener(MainActivity myMapViewer) {
		this.myMapViewer = myMapViewer;
		this.positionMarina = new Location("");
		this.positionMarina.setLatitude(marinaLatitude);
		this.positionMarina.setLongitude(marinaLongitude);
	}

	@Override
	public void onLocationChanged(Location location) {
		//Get myPosition
		GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());

		//Update myPosition on the map
		this.myMapViewer.overlayCircle.setCircleData(point, location.getAccuracy());
		this.myMapViewer.myPositionItem.setPoint(point);
		this.myMapViewer.mapView.updatePosition();

		//Update the orientation
		//if (this.centerAtFirstFix) this.myMapViewer.mySensorListener.registerSensor();

		//Center the map on myPosition
		if (this.isCenterAtFirstFix() || this.myMapViewer.isSnapToLocationEnabled()) {
			this.centerAtFirstFix = false;
			this.myMapViewer.mapView.getController().setCenter(point);

			//Set speed
			if(location.hasSpeed() && location.getSpeed()!=0.0){
				this.myMapViewer.infoSpeed.setText(location.getSpeed()+" m/s");

				float distance = location.getSpeed() * TIME;
				double sizeOfArrowForSpeed = this.distanceToPixel(distance);
			}

			//Set bearing
			if(location.hasBearing() && location.getBearing()!=0.0){
				this.myMapViewer.infoBearing.setText(location.getBearing()+ "°");
				Drawable c = rotateDrawable(location.getBearing());
				myMapViewer.myPositionItem.setMarker(ItemizedOverlay.boundCenter(c));
				this.myMapViewer.mapView.updatePosition();
			}
			this.myMapViewer.infoPosition.setText("lat: "+location.getLatitude()+"°, lon: "+location.getLongitude()+"°");

			//Scale according to the distance
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

	/**
	 * @return true if map should be centered to the first fix;
	 * 		else false
	 */
	boolean isCenterAtFirstFix() {
		return this.centerAtFirstFix;
	}

	/**
	 * Set the "show my location" mode.
	 * 
	 * @param centerAtFirstFix
	 *            defines whether the map should be centered to the first fix.
	 */
	void setCenterAtFirstFix(boolean centerAtFirstFix) {
		this.centerAtFirstFix = centerAtFirstFix;
	}

	/** 
	 * @param angle
	 *            defines the angle to the drawable for myPosition image.
	 * @return a BitmapDrawable of myPosition with the angle
	 */
	public BitmapDrawable rotateDrawable(float angle)
	{
		// Get the bitmap of myPosition
		Bitmap arrowBitmap = BitmapFactory.decodeResource(this.myMapViewer.getResources(), R.drawable.bateau);

		// Create blank bitmap of equal size
		Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
		canvasBitmap.eraseColor(0x00000000);

		// Create canvas
		Canvas canvas = new Canvas(canvasBitmap);

		// Create rotation matrix with angle
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate(angle, canvas.getWidth()/2, canvas.getHeight()/2);

		// Draw bitmap onto canvas using matrix
		canvas.drawBitmap(arrowBitmap, rotateMatrix, null);

		return new BitmapDrawable(canvasBitmap); 
	}

	/**
	 * @param distance
	 *            defines the distance between myPostion and "Marina du Château" in meters
	 */
	private float distanceToPixel(float distance){
		MapPosition mapPosition = this.myMapViewer.mapView.getMapPosition().getMapPosition();
		double groundResolution = MercatorProjection.calculateGroundResolution(mapPosition.geoPoint.getLatitude(),
				mapPosition.zoomLevel);

		int[] scaleBarValues;
		if (this.myMapViewer.mapView.getMapScaleBar().isImperialUnits()) {
			groundResolution = groundResolution / METER_FOOT_RATIO;
			scaleBarValues = SCALE_BAR_VALUES_IMPERIAL;
		} else {
			scaleBarValues = SCALE_BAR_VALUES_METRIC;
		}

		float scaleBarLength = 0;
		int mapScaleValue = 0;

		for (int i = 0; i < scaleBarValues.length; ++i) {
			mapScaleValue = scaleBarValues[i];
			scaleBarLength = mapScaleValue / (float) groundResolution;
			if (scaleBarLength < (BITMAP_WIDTH - 10)) {
				break;
			}
		}

		float scaleValue;
		if(this.myMapViewer.mapView.getMapScaleBar().isImperialUnits()){
			scaleValue = mapScaleValue * MILE_TO_METER;
		}
		else{
			scaleValue = mapScaleValue;
		}

		return (scaleBarLength*distance)/scaleValue;
	}
}
