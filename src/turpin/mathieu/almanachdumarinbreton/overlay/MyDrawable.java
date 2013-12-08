package turpin.mathieu.almanachdumarinbreton.overlay;

import org.mapsforge.core.GeoPoint;

import android.graphics.Paint;
import android.graphics.Point;

public abstract class MyDrawable{

    private final Paint paint;
    
    /**
	 * Cached position of the circle on the map.
	 */
	Point cachedCenterPosition;
	
	/**
	 * Geographical coordinate of the circle.
	 */
	protected GeoPoint center;
	
	/**
	 * Zoom level of the cached circle position.
	 */
	byte cachedZoomLevel;
	
	private float positionX = 0;
	private float positionY = 0;
	private float rotation = 0;

    public MyDrawable(GeoPoint center) {
    	this.center = center;
        this.paint = new Paint();
        initPaint();
    }
    
    Paint getPaint(){
    	return paint;
    }
    
    protected abstract void initPaint();
        
    public void setRotation(float rotation){
    	this.rotation = rotation;
    }
    
    float getRotation(){
    	return rotation;
    }
    
    float getPositionX(){
    	return positionX;
    }
    
    float getPositionY(){
    	return positionY;
    }
    
    void setPosition(float positionX,float positionY){
		this.positionX = positionX;
		this.positionY = positionY;
    }
}