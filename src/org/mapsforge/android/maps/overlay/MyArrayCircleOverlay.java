package org.mapsforge.android.maps.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.OverlayCircle;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class MyArrayCircleOverlay{
	private static final int INITIAL_CAPACITY = 8;
	
	private final Point circlePosition;
	private final Paint defaultPaintFill;
	private final Paint defaultPaintOutline;
	private final boolean hasDefaultPaint;
	private final Path path;
	private List<Integer> visibleCircles;
	private List<Integer> visibleCirclesRedraw;
	private final List<OverlayCircle> overlayCircles;
	
	/**
	 * @param defaultPaintFill
	 *            the default paint which will be used to fill the circles (may be null).
	 * @param defaultPaintOutline
	 *            the default paint which will be used to draw the circle outlines (may be null).
	 */
	public MyArrayCircleOverlay(Paint defaultPaintFill, Paint defaultPaintOutline) {
		this.defaultPaintFill = defaultPaintFill;
		this.defaultPaintOutline = defaultPaintOutline;
		this.hasDefaultPaint = defaultPaintFill != null || defaultPaintOutline != null;
		this.circlePosition = new Point();
		this.visibleCircles = new ArrayList<Integer>(INITIAL_CAPACITY);
		this.visibleCirclesRedraw = new ArrayList<Integer>(INITIAL_CAPACITY);
		this.path = new Path();
		this.overlayCircles = new ArrayList<OverlayCircle>(INITIAL_CAPACITY);
	}

	public void drawOverlayCircle(Canvas canvas, Point drawPosition, Projection projection, byte drawZoomLevel){
		// erase the list of visible circles
		this.visibleCirclesRedraw.clear();

		int numberOfCircles = overlayCircles.size();
		for (int circleIndex = 0; circleIndex < numberOfCircles; ++circleIndex) {

			// get the current circle
			OverlayCircle overlayCircle = createCircle(circleIndex);
			if (overlayCircle == null) {
				continue;
			}

			synchronized (overlayCircle) {
				// make sure that the current circle has a center position and a radius
				if (overlayCircle.center == null || overlayCircle.radius < 0) {
					continue;
				}

				// make sure that the cached center position is valid
				if (drawZoomLevel != overlayCircle.cachedZoomLevel) {
					overlayCircle.cachedCenterPosition = projection.toPoint(overlayCircle.center,
							overlayCircle.cachedCenterPosition, drawZoomLevel);
					overlayCircle.cachedZoomLevel = drawZoomLevel;
					overlayCircle.cachedRadius = projection.metersToPixels(overlayCircle.radius, drawZoomLevel);
				}

				// calculate the relative circle position on the canvas
				this.circlePosition.x = overlayCircle.cachedCenterPosition.x - drawPosition.x;
				this.circlePosition.y = overlayCircle.cachedCenterPosition.y - drawPosition.y;
				float circleRadius = overlayCircle.cachedRadius;

				// check if the bounding box of the circle intersects with the canvas
				if ((this.circlePosition.x + circleRadius) >= 0
						&& (this.circlePosition.x - circleRadius) <= canvas.getWidth()
						&& (this.circlePosition.y + circleRadius) >= 0
						&& (this.circlePosition.y - circleRadius) <= canvas.getHeight()) {
					// assemble the path
					this.path.reset();
					this.path.addCircle(this.circlePosition.x, this.circlePosition.y, circleRadius, Path.Direction.CCW);

					if (overlayCircle.hasPaint || this.hasDefaultPaint) {
						drawPathOnCanvas(canvas, overlayCircle); //ICI

						// add the current circle index to the list of visible circles
						this.visibleCirclesRedraw.add(Integer.valueOf(circleIndex));
					}
				}
			}
		}

		// swap the two visible circle lists
		synchronized (this.visibleCircles) {
			List<Integer> visibleCirclesTemp = this.visibleCircles;
			this.visibleCircles = this.visibleCirclesRedraw;
			this.visibleCirclesRedraw = visibleCirclesTemp;
		}
	}
	
	private void drawPathOnCanvas(Canvas canvas, OverlayCircle overlayCircle) {
		if (overlayCircle.hasPaint) {
			// use the paints from the current circle
			if (overlayCircle.paintOutline != null) {
				canvas.drawPath(this.path, overlayCircle.paintOutline);
			}
			if (overlayCircle.paintFill != null) {
				canvas.drawPath(this.path, overlayCircle.paintFill);
			}
		} else if (this.hasDefaultPaint) {
			// use the default paint objects
			if (this.defaultPaintOutline != null) {
				canvas.drawPath(this.path, this.defaultPaintOutline);
			}
			if (this.defaultPaintFill != null) {
				canvas.drawPath(this.path, this.defaultPaintFill);
			}
		}
	}
	
	/**
	 * Adds the given circle to the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be added to the overlay.
	 */
	public void addCircle(OverlayCircle overlayCircle) {
		synchronized (this.overlayCircles) {
			this.overlayCircles.add(overlayCircle);
		}
	}

	/**
	 * Adds all circles of the given collection to the overlay.
	 * 
	 * @param c
	 *            collection whose circles should be added to the overlay.
	 */
	public void addCircles(Collection<? extends OverlayCircle> c) {
		synchronized (this.overlayCircles) {
			this.overlayCircles.addAll(c);
		}
	}

	/**
	 * Removes all circles from the overlay.
	 */
	public void clear() {
		synchronized (this.overlayCircles) {
			this.overlayCircles.clear();
		}
	}

	/**
	 * Removes the given circle from the overlay.
	 * 
	 * @param overlayCircle
	 *            the circle that should be removed from the overlay.
	 */
	public void removeCircle(OverlayCircle overlayCircle) {
		synchronized (this.overlayCircles) {
			this.overlayCircles.remove(overlayCircle);
		}
	}

	public int size() {
		synchronized (this.overlayCircles) {
			return this.overlayCircles.size();
		}
	}

	protected OverlayCircle createCircle(int index) {
		synchronized (this.overlayCircles) {
			if (index >= this.overlayCircles.size()) {
				return null;
			}
			return this.overlayCircles.get(index);
		}
	}
}
