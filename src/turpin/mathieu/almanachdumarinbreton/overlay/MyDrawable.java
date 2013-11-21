package turpin.mathieu.almanachdumarinbreton.overlay;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public abstract class MyDrawable extends Drawable {

    private final Paint paint;
	private float positionX = 0;
	private float positionY = 0;
	private float rotation = 0;

    public MyDrawable() {
        this.paint = new Paint();
        initPaint();
    }
    
    Paint getPaint(){
    	return paint;
    }
    
    protected abstract void initPaint();
        
    void setRotation(float rotation){
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
    
    @Override
    public abstract void draw(Canvas canvas);

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