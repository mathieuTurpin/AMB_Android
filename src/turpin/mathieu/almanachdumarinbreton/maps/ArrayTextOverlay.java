package turpin.mathieu.almanachdumarinbreton.maps;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class ArrayTextOverlay extends ArrayItemizedOverlay{

	/**
	 * @param defaultMarker
	 *            the default marker (may be null). This marker is aligned to the center of its bottom line to allow for
	 *            a conical symbol such as a pin or a needle.
	 */
	public ArrayTextOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}

	/**
	 * @param defaultMarker
	 *            the default marker (may be null).
	 * @param alignMarker
	 *            whether the default marker should be aligned or not. If true, the marker is aligned to the center of
	 *            its bottom line to allow for a conical symbol such as a pin or a needle.
	 */
	public ArrayTextOverlay(Drawable defaultMarker, boolean alignMarker) {
		super(defaultMarker,alignMarker);
	}

	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point drawPosition, Projection projection, byte drawZoomLevel){
		//Update position of all OverlayItem
		for(int itemIndex = 0; itemIndex<this.size(); itemIndex++){
			OverlayItem overlayItem = createItem(itemIndex);
			if (overlayItem == null) {
				continue;
			}
			TextDrawable text = (TextDrawable) overlayItem.getMarker();
			text.setPosition(drawPosition.x, drawPosition.y, drawZoomLevel);
		}
		super.drawOverlayBitmap(canvas, drawPosition, projection, drawZoomLevel);
	}
}
