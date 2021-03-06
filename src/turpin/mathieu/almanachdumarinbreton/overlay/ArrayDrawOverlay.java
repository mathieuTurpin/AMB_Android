package turpin.mathieu.almanachdumarinbreton.overlay;

import java.util.Collection;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.MyArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.Overlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;


import android.graphics.Canvas;
import android.graphics.Point;

public class ArrayDrawOverlay extends Overlay{

	private MyArrayCircleOverlay circleOverlays;
	private MyArrayTextOverlay textOverlays;
	
	public ArrayDrawOverlay(){
		this.circleOverlays = new MyArrayCircleOverlay(null, null);
		this.textOverlays = new MyArrayTextOverlay(null);
		populate();
	}
	
	/**
	 * Adds the given circle to the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be added to the overlay.
	 */
	public void addCircle(OverlayCircle overlayCircle) {
		circleOverlays.addCircle(overlayCircle);
		populate();
	}

	/**
	 * Adds all circles of the given collection to the overlay.
	 * 
	 * @param c
	 *            collection whose circles should be added to the overlay.
	 */
	public void addCircles(Collection<? extends OverlayCircle> c) {
		circleOverlays.addCircles(c);
		populate();
	}

	/**
	 * Removes all circles from the overlay.
	 */
	public void clearCircle() {
		this.circleOverlays.clear();
		populate();
	}

	/**
	 * Removes the given circle from the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be removed from the overlay.
	 */
	public void removeCircle(OverlayCircle overlayCircle) {
		this.circleOverlays.removeCircle(overlayCircle);
		populate();
	}
	
	/**
	 * Adds the given circle to the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be added to the overlay.
	 */
	public void addText(OverlayText overlayText) {
		textOverlays.addText(overlayText);
		populate();
	}

	/**
	 * Adds all circles of the given collection to the overlay.
	 * 
	 * @param c
	 *            collection whose circles should be added to the overlay.
	 */
	public void addTexts(Collection<? extends OverlayText> c) {
		textOverlays.addTexts(c);
		populate();
	}

	/**
	 * Removes all circles from the overlay.
	 */
	public void clearText() {
		this.textOverlays.clear();
		populate();
	}

	/**
	 * Removes the given circle from the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be removed from the overlay.
	 */
	public void removeText(OverlayText overlayText) {
		this.textOverlays.removeText(overlayText);
		populate();
	}
	
	/**
	 * Removes the given circle from the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be removed from the overlay.
	 */
	public void removeTexts(Collection<? extends OverlayText> c) {
		this.textOverlays.removeTexts(c);
		populate();
	}

	@Override
	protected void drawOverlayBitmap(Canvas canvas, Point drawPosition, Projection projection, byte drawZoomLevel){
		this.circleOverlays.drawOverlayCircle(canvas, drawPosition, projection, drawZoomLevel);
		this.textOverlays.drawOverlayText(canvas, drawPosition, projection, drawZoomLevel);
	}
	
	/**
	 * This method should be called after circles have been added to the overlay.
	 */
	protected final void populate() {
		super.requestRedraw();
	}
}
