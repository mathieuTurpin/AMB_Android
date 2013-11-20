package turpin.mathieu.almanachdumarinbreton.maps;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.MercatorProjection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;
	private final OverlayItem item;
	private float positionX = 0;
	private float positionY = 0;
	private float rotation = 0;

    public TextDrawable(OverlayItem item,String text) {
    	this.item = item;
        this.text = text;

        this.paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
    }
    
    public void setRotation(float rotation){
    	this.rotation = rotation;
    }
    
    public void setPosition(float longitude,float latitude,byte zoomLevel){
    	float pixelLeft = (float) MercatorProjection.longitudeToPixelX(item.getPoint().getLongitude(), zoomLevel);
		float pixelTop = (float) MercatorProjection.latitudeToPixelY(item.getPoint().getLatitude(),zoomLevel);
		
		positionX = pixelLeft - longitude;
		positionY = pixelTop - latitude;
    }
    
    @Override
    public void draw(Canvas canvas) {
    	canvas.save();
    	canvas.rotate(rotation, positionX, positionY);
    	canvas.drawText(text, positionX, positionY, paint);
    	canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}