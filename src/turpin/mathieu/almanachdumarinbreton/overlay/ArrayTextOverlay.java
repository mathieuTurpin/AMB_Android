package turpin.mathieu.almanachdumarinbreton.overlay;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;


import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class ArrayTextOverlay extends ArrayItemizedOverlay{

	/**
	 * @param defaultMarker
	 *            the default marker (will be null). This marker is aligned to the center of its bottom line to allow for
	 *            a conical symbol such as a pin or a needle.
	 */
	public ArrayTextOverlay(Drawable defaultMarker) {
		super(null);
	}

	/**
	 * @param defaultMarker
	 *            the default marker (will be null).
	 * @param alignMarker
	 *            whether the default marker should be aligned or not. If true, the marker is aligned to the center of
	 *            its bottom line to allow for a conical symbol such as a pin or a needle.
	 */
	public ArrayTextOverlay(Drawable defaultMarker, boolean alignMarker) {
		super(null,alignMarker);
	}

	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point drawPosition, Projection projection, byte drawZoomLevel){
		//Update position of all MyOverlay
		for(int itemIndex = 0; itemIndex<this.size(); itemIndex++){
			MyOverlay overlayItem = (MyOverlay) createItem(itemIndex);
			if (overlayItem == null) {
				continue;
			}
			overlayItem.setPositionMarker(drawPosition.x, drawPosition.y, drawZoomLevel);
		}
		super.drawOverlayBitmap(canvas, drawPosition, projection, drawZoomLevel);
	}
}
