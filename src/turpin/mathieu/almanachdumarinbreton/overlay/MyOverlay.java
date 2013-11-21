package turpin.mathieu.almanachdumarinbreton.overlay;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MercatorProjection;


import android.graphics.drawable.Drawable;

public class MyOverlay extends OverlayItem{

	private MyDrawable myMarkerDrawable;
	
	public MyOverlay(){
		super();
	}
	
	public MyOverlay(GeoPoint point, String title, String snippet) {
		super(point,title,snippet);
	}

	public MyOverlay(GeoPoint point, String title, String snippet, MyDrawable marker) {
		super(point,title,snippet);
		this.myMarkerDrawable = marker;
	}

	@Override
	public synchronized MyDrawable getMarker() {
		return this.myMarkerDrawable;
	}

	@Deprecated
	@Override
	public synchronized void setMarker(Drawable marker) {
		  throw new UnsupportedOperationException("not supported");
	}
	
	public synchronized void setMarker(MyDrawable marker) {
		this.myMarkerDrawable = marker;
	}
	
	public void setPositionMarker(float longitude, float latitude, byte zoomLevel){
		float pixelLeft = (float) MercatorProjection.longitudeToPixelX(this.getPoint().getLongitude(), zoomLevel);
		float pixelTop = (float) MercatorProjection.latitudeToPixelY(this.getPoint().getLatitude(),zoomLevel);
		
		float positionX = pixelLeft - longitude;
		float positionY = pixelTop - latitude;
		this.myMarkerDrawable.setPosition(positionX,positionY);
	}
	
	public void setRotationMarker(float rotation){
		this.myMarkerDrawable.setRotation(rotation);
	}
}
