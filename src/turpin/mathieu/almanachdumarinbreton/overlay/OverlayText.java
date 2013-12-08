package turpin.mathieu.almanachdumarinbreton.overlay;

import org.mapsforge.core.GeoPoint;

import android.graphics.Color;
import android.graphics.Paint;

public class OverlayText extends MyDrawable {

    private final String text;

    public OverlayText(GeoPoint center,String text) {
    	super(center);
        this.text = text;
    }

    String getText(){
    	return text;
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