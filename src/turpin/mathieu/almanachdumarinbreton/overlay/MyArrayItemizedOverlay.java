package turpin.mathieu.almanachdumarinbreton.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class MyArrayItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private final Context context;
	private static final int INITIAL_CAPACITY = 30;
	private static final String THREAD_NAME = "MyArrayItemizedOverlay";

	private final List<OverlayItem> overlayItemsDisplay;
	private final ArrayList<OverlayItem> overlayService;
	private final ArrayList<OverlayItem> overlayOSM;

	/**
	 * @param context
		          the reference to the application context.
	 * @param defaultMarker
	 *            the default marker (may be null). This marker is aligned to the center of its bottom line to allow for
	 *            a conical symbol such as a pin or a needle.
	 */
	public MyArrayItemizedOverlay(Context context, Drawable defaultMarker) {
		this(context,defaultMarker,true);
	}

	/**
	 * @param context
		          the reference to the application context.
	 * @param defaultMarker
	 *            the default marker (may be null).
	 * @param alignMarker
	 *            whether the default marker should be aligned or not. If true, the marker is aligned to the center of
	 *            its bottom line to allow for a conical symbol such as a pin or a needle.
	 */
	public MyArrayItemizedOverlay(Context context, Drawable defaultMarker, boolean alignMarker) {
		super(defaultMarker != null && alignMarker ? ItemizedOverlay.boundCenterBottom(defaultMarker) : defaultMarker);
		this.context = context;
		this.overlayItemsDisplay = new ArrayList<OverlayItem>(2*INITIAL_CAPACITY);
		this.overlayService = new ArrayList<OverlayItem>(INITIAL_CAPACITY);
		this.overlayOSM = new ArrayList<OverlayItem>(INITIAL_CAPACITY);
	}

	public boolean checkContainsOSM(String title){
		for(int itemIndex = 0; itemIndex<this.size(); itemIndex++){
			OverlayItem overlayItem = createItemOSM(itemIndex);
			if (overlayItem == null) {
				continue;
			}
			if(overlayItem.getTitle().equals(title)){
				return true;
			}
		}
		return false;
	}


	/**
	 * Handles a tap event.
	 * <p>
	 * Show an {@link AlertDialog} that gives information
	 * 
	 * @param index
	 *            the index of the item that has been tapped.
	 * @return true if the event was handled, false otherwise.
	 */
	protected boolean onTap(int index) {
		OverlayItem item = createItem(index);

		if (item != null && !item.getSnippet().equals("noTap")) {
			double lat = item.getPoint().getLatitude();
			double lon = item.getPoint().getLongitude();
			InfoOverlayItemDialog dialog = InfoOverlayItemDialog.getInstance(item.getTitle(),item.getSnippet(),lat,lon);
			Activity activity = (Activity) this.context;
			dialog.show(activity.getFragmentManager(), "InfoOverlayItemDialog");
			return true;
		}
		return false;
	}

	/**
	 * Adds the given item to the overlay.
	 * 
	 * @param overlayItem
	 *            the item that should be added to the overlay.
	 */
	public void addItem(OverlayItem overlayItem,boolean first) {
		synchronized (this.overlayItemsDisplay) {
			if(first) this.overlayItemsDisplay.add(0,overlayItem);
			else this.overlayItemsDisplay.add(overlayItem);
		}
		populate();
	}	

	/**
	 * Adds the given item to the overlay.
	 * 
	 * @param overlayItem
	 *            the item that should be added to the overlay.
	 */
	public void addItemOSM(OverlayItem overlayItem) {
		synchronized (this.overlayOSM) {
			this.overlayOSM.add(overlayItem);
		}

		addItem(overlayItem,true);
	}

	public void addItemService(OverlayItem overlayItem) {
		synchronized (this.overlayService) {
			this.overlayService.add(overlayItem);
		}

		addItem(overlayItem,false);
	}


	/**
	 * Adds all items of the given collection to the overlay.
	 * 
	 * @param c
	 *            collection whose items should be added to the overlay.
	 */
	public void addItemsOSM(Collection<? extends OverlayItem> c) {
		synchronized (this.overlayOSM) {
			this.overlayOSM.addAll(c);
		}
		synchronized (this.overlayItemsDisplay) {
			this.overlayItemsDisplay.addAll(0,c);
		}
		populate();
	}

	public void initItemsService(Collection<? extends OverlayItem> c) {
		synchronized (this.overlayService) {
			this.overlayService.addAll(c);
		}
	}

	public void addItemsService(Collection<? extends OverlayItem> c) {
		synchronized (this.overlayService) {
			this.overlayService.addAll(c);
		}
		synchronized (this.overlayItemsDisplay) {
			this.overlayItemsDisplay.addAll(c);
		}
		populate();
	}

	/**
	 * Removes all items from the overlay.
	 */
	public void clearOSM() {
		hiddenOSM();
		synchronized (this.overlayOSM) {
			this.overlayOSM.clear();
		}
		populate();
	}

	public void clearService() {
		hiddenService();
		synchronized (this.overlayService) {
			this.overlayService.clear();
		}
		populate();
	}

	/**
	 * Removes all items from all list.
	 */
	public void clearAll() {
		clearOSM();
		clearService();
	}

	@Override
	public String getThreadName() {
		return THREAD_NAME;
	}

	/**
	 * Removes the given item from the overlay.
	 * 
	 * @param overlayItem
	 *            the item that should be removed from the overlay.
	 */
	private void removeItem(OverlayItem overlayItem) {
		synchronized (this.overlayItemsDisplay) {
			this.overlayItemsDisplay.remove(overlayItem);
		}
		populate();
	}

	public void removeItemOSM(OverlayItem overlayItem) {
		synchronized (this.overlayOSM) {
			this.overlayOSM.remove(overlayItem);
		}
		removeItem(overlayItem);
	}

	public void removeItemService(OverlayItem overlayItem) {
		synchronized (this.overlayService) {
			this.overlayService.remove(overlayItem);
		}
		removeItem(overlayItem);
	}

	public void displayOSM(){
		if(overlayOSM == null || this.overlayItemsDisplay.containsAll(overlayOSM)) return;
		this.addItemsOSM(overlayOSM);
	}

	public void hiddenOSM(){
		synchronized (this.overlayOSM) {
			synchronized (this.overlayItemsDisplay) {
				this.overlayItemsDisplay.removeAll(overlayOSM);
			}
		}
		populate();
	}

	public void displayService(){
		if(overlayService == null || this.overlayItemsDisplay.containsAll(overlayService)) return;
		this.addItemsService(overlayService);
	}

	public void hiddenService(){
		synchronized (this.overlayService) {
			synchronized (this.overlayItemsDisplay) {
				this.overlayItemsDisplay.removeAll(overlayService);
			}
		}
		populate();
	}

	@Override
	public int size() {
		synchronized (this.overlayItemsDisplay) {
			return this.overlayItemsDisplay.size();
		}
	}

	//GetItem to display in ItemizedOverlay
	@Override
	protected OverlayItem createItem(int index) {
		synchronized (this.overlayItemsDisplay) {
			if (index >= this.overlayItemsDisplay.size()) {
				return null;
			}
			return this.overlayItemsDisplay.get(index);
		}
	}

	protected OverlayItem createItemOSM(int index) {
		synchronized (this.overlayOSM) {
			if (index >= this.overlayOSM.size()) {
				return null;
			}
			return this.overlayOSM.get(index);
		}
	}


}
