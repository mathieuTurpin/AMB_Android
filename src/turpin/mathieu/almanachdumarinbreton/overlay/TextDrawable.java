package turpin.mathieu.almanachdumarinbreton.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class TextDrawable extends MyDrawable {

    private final String text;

    public TextDrawable(String text) {
    	super();
        this.text = text;
    }

    String getText(){
    	return text;
    }
    
    @Override
    public void draw(Canvas canvas) {
    	canvas.save();
    	canvas.rotate(getRotation(), getPositionX(), getPositionY());
    	canvas.drawText(text, getPositionX(), getPositionY(), getPaint());
    	canvas.restore();
    }

	@Override
	protected void initPaint() {
		Paint paint = getPaint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(25);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
	}
}