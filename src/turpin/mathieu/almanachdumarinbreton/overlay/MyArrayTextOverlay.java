package turpin.mathieu.almanachdumarinbreton.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapsforge.android.maps.Projection;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class MyArrayTextOverlay{
	private static final int INITIAL_CAPACITY = 8;
	
	private final Point textPosition;
	private final Paint defaultPaint;
	private List<Integer> visibleText;
	private List<Integer> visibleTextRedraw;
	private final List<OverlayText> overlayTexts;
	
	/**
	 * @param defaultPaintFill
	 *            the default paint which will be used to fill the circles (may be null).
	 * @param defaultPaintOutline
	 *            the default paint which will be used to draw the circle outlines (may be null).
	 */
	public MyArrayTextOverlay(Paint defaultPaint) {
		this.defaultPaint = defaultPaint;
		this.textPosition = new Point();
		this.visibleText = new ArrayList<Integer>(INITIAL_CAPACITY);
		this.visibleTextRedraw = new ArrayList<Integer>(INITIAL_CAPACITY);
		
		this.overlayTexts = new ArrayList<OverlayText>(INITIAL_CAPACITY);
	}

	public void drawOverlayText(Canvas canvas, Point drawPosition, Projection projection, byte drawZoomLevel){
		// erase the list of visible texts
		this.visibleTextRedraw.clear();

		int numberOfText = overlayTexts.size();
		for (int textIndex = 0; textIndex < numberOfText; ++textIndex) {

			// get the current text
			OverlayText overlayText = createText(textIndex);
			if (overlayText == null) {
				continue;
			}

			synchronized (overlayText) {
				// make sure that the current OverlayText has a text
				if (overlayText.getText() == null) {
					continue;
				}

				// make sure that the cached center position is valid
				if (drawZoomLevel != overlayText.cachedZoomLevel) {
					overlayText.cachedCenterPosition = projection.toPoint(overlayText.center,
							overlayText.cachedCenterPosition, drawZoomLevel);
					overlayText.cachedZoomLevel = drawZoomLevel;
				}

				// calculate the relative text position on the canvas
				this.textPosition.x = overlayText.cachedCenterPosition.x - drawPosition.x;
				this.textPosition.y = overlayText.cachedCenterPosition.y - drawPosition.y;

				if (overlayText.getPaint()!=null || this.defaultPaint!=null) {
					
					if (overlayText.getPaint()!=null) {
						canvas.save();
						canvas.rotate(overlayText.getRotation(), textPosition.x, textPosition.y);
						canvas.drawText(overlayText.getText(), textPosition.x, textPosition.y,overlayText.getPaint());
						canvas.restore();
					} else if (this.defaultPaint != null) {
						canvas.save();
						canvas.rotate(overlayText.getRotation(), textPosition.x, textPosition.y);
						canvas.drawText(overlayText.getText(), textPosition.x, textPosition.y,this.defaultPaint);
						canvas.restore();
					}
					
					// add the current text index to the list of visible text
					this.visibleTextRedraw.add(Integer.valueOf(textIndex));
				}
			}
		}

		// swap the two visible circle lists
		synchronized (this.visibleText) {
			List<Integer> visibleTextTemp = this.visibleText;
			this.visibleText = this.visibleTextRedraw;
			this.visibleTextRedraw = visibleTextTemp;
		}
	}

	
	/**
	 * Adds the given circle to the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be added to the overlay.
	 */
	public void addText(OverlayText overlayText) {
		synchronized (this.overlayTexts) {
			this.overlayTexts.add(overlayText);
		}
	}

	/**
	 * Adds all circles of the given collection to the overlay.
	 * 
	 * @param c
	 *            collection whose circles should be added to the overlay.
	 */
	public void addTexts(Collection<? extends OverlayText> c) {
		synchronized (this.overlayTexts) {
			this.overlayTexts.addAll(c);
		}
	}

	/**
	 * Removes all circles from the overlay.
	 */
	public void clear() {
		synchronized (this.overlayTexts) {
			this.overlayTexts.clear();
		}
	}

	/**
	 * Removes the given circle from the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be removed from the overlay.
	 */
	public void removeText(OverlayText overlayText) {
		synchronized (this.overlayTexts) {
			this.overlayTexts.remove(overlayText);
		}
	}
	
	/**
	 * Removes the given circle from the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be removed from the overlay.
	 */
	public void removeTexts(Collection<? extends OverlayText> c) {
		synchronized (this.overlayTexts) {
			this.overlayTexts.removeAll(c);
		}
	}

	public int size() {
		synchronized (this.overlayTexts) {
			return this.overlayTexts.size();
		}
	}

	protected OverlayText createText(int index) {
		synchronized (this.overlayTexts) {
			if (index >= this.overlayTexts.size()) {
				return null;
			}
			return this.overlayTexts.get(index);
		}
	}
}
