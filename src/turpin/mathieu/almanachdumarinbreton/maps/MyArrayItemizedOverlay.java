package turpin.mathieu.almanachdumarinbreton.maps;

import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.graphics.drawable.Drawable;

public class MyArrayItemizedOverlay extends ArrayItemizedOverlay {
	/**
	 * @param defaultMarker
	 *            the default marker (may be null). This marker is aligned to the center of its bottom line to allow for
	 *            a conical symbol such as a pin or a needle.
	 */
	public MyArrayItemizedOverlay(Drawable defaultMarker) {
		super(defaultMarker);
	}

	/**
	 * @param defaultMarker
	 *            the default marker (may be null).
	 * @param alignMarker
	 *            whether the default marker should be aligned or not. If true, the marker is aligned to the center of
	 *            its bottom line to allow for a conical symbol such as a pin or a needle.
	 */
	public MyArrayItemizedOverlay(Drawable defaultMarker, boolean alignMarker) {
		super(defaultMarker,alignMarker);
	}
	
	public boolean checkContains(OverlayItem item){
		int i = 0;
		OverlayItem currentItem = createItem(i);
		while(currentItem!=null){
			if(currentItem.getTitle().equals(item.getTitle())){
				return true;
			}
			i++;
			currentItem = createItem(i);
		}
		return false;
	}
	
}
