package org.mapsforge.android.maps;

import java.io.File;
import java.util.ArrayList;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.mapgenerator.InMemoryTileCache;
import org.mapsforge.android.maps.mapgenerator.JobParameters;
import org.mapsforge.android.maps.mapgenerator.JobQueue;
import org.mapsforge.android.maps.mapgenerator.MapGenerator;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorFactory;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.android.maps.mapgenerator.MapWorker;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.TileDownloader;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.core.MapPosition;
import org.mapsforge.core.MercatorProjection;
import org.mapsforge.core.Tile;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoisDTOList;
import turpin.mathieu.almanachdumarinbreton.MyXmlParser;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.GetPoiAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.GetPoiInfoAsyncTask;
import turpin.mathieu.almanachdumarinbreton.asynctask.poi.GetPoiAsyncTask.GetPoiListener;
import turpin.mathieu.almanachdumarinbreton.forum.AddPoiDialog;
import turpin.mathieu.almanachdumarinbreton.maps.FileSystemTileCacheOpenSeaMap;
import turpin.mathieu.almanachdumarinbreton.maps.InMemoryTileCacheOpenSeaMap;
import turpin.mathieu.almanachdumarinbreton.maps.MyMapWorker;
import turpin.mathieu.almanachdumarinbreton.maps.OpenSeaMapTileDownloader;
import turpin.mathieu.almanachdumarinbreton.overlay.ArrayDrawOverlay;
import turpin.mathieu.almanachdumarinbreton.overlay.MyArrayItemizedOverlay;
import turpin.mathieu.almanachdumarinbreton.overlay.OverlayText;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A MyMapView shows a map on the display of the device. It handles all user input and touch gestures to move and zoom the
 * map. This MyMapView also includes a scale bar and zoom controls. The {@link #getController()} method returns a
 * {@link MapController} to programmatically modify the position and zoom level of the map.
 * <p>
 * This implementation supports offline map rendering as well as downloading map images (tiles) over an Internet
 * connection. The operation mode of a MyMapView can be set in the constructor and changed at runtime with the
 * {@link #setMapGeneratorInternal(MapGenerator)} method. Some MapView parameters depend on the selected operation mode.
 * <p>
 * In offline rendering mode a special database file is required which contains the map data. Map files can be stored in
 * any folder. The current map file is set by calling {@link #setMapFile(File)}. To retrieve the current
 * {@link MapDatabase}, use the {@link #getMapDatabase()} method.
 * <p>
 * {@link Overlay Overlays} can be used to display geographical data such as points and ways. To draw an overlay on top
 * of the map, add it to the list returned by {@link #getOverlays()}.
 */
public class MyMapView extends MapView implements GetPoiListener{
	private static final float DEFAULT_TEXT_SCALE = 1;
	private static final int DEFAULT_TILE_CACHE_SIZE_FILE_SYSTEM = 100;
	private static final int DEFAULT_TILE_CACHE_SIZE_IN_MEMORY = 10;

	//Position where should put Overlay for OpenSeaMap in a MyMapView
	private final static int DEFAULT_OVERLAY = 0;

	//Job Queue for OpenSeaMap
	private final JobQueue jobQueueOpenSeaMap = new JobQueue(this);

	private JobParameters myJobParameters;

	//MyMapWorker that controls download tiles from OpenStreetMap Server
	private final MapWorker myMapWorker;

	//Cache for OpenStreetMap
	private InMemoryTileCache inMemoryTileCacheOpenStreetMap;

	//Cache for OpenSeaMap
	private final InMemoryTileCacheOpenSeaMap inMemoryTileCacheOpenSeaMap;
	private final FileSystemTileCacheOpenSeaMap fileSystemTileCacheOpenSeaMap;

	//MyMapWorker that controls download tiles from OpenSeaMap Server
	private final MyMapWorker mapWorkerOpenSeaMap;

	//ArrayItemizedOverlay that gets overlay make with tiles from OpenSeaMap Server for the current zoom
	private final MyArrayItemizedOverlay overlayOpenSeaMap;	
	private final ArrayDrawOverlay overlayDraw;
	private ArrayList<OverlayText> textOverlay;
	private ArrayList<OverlayText> soundingOverlay;

	private byte zoomCache;

	private boolean isEnableShowBaliseOSM;
	private boolean isEnableShowService;
	private boolean isEnableShowPoi;
	private boolean isEnableShowText;
	private boolean isEnableShowSounding;

	/**
	 * Keep a record of the center of the map, to know if the map
	 * has been panned.
	 */
	private GeoPoint lastMapCenter;

	private float previousPositionX;
	private float previousPositionY;

	/**
	 * @param context
	 *            the enclosing {@link MapActivity} instance.
	 * @throws IllegalArgumentException
	 *             if the context object is not an instance of {@link MapActivity}.
	 */
	public MyMapView(Context context) {
		this(context,new DatabaseRenderer());		
	}

	/**
	 * Need to test
	 * @param context
	 *            the enclosing {@link MapActivity} instance.
	 * @param attributeSet
	 *            a set of attributes.
	 * @throws IllegalArgumentException
	 *             if the context object is not an instance of {@link MapActivity}.
	 */
	public MyMapView(Context context, AttributeSet attributeSet) {
		this(context,MapGeneratorFactory.createMapGenerator(attributeSet));
	}

	/**
	 * @param context
	 *            the enclosing {@link MapActivity} instance.
	 * @param mapGenerator
	 *            the {@link MapGenerator} for this {@link MyMapView}.
	 * @throws IllegalArgumentException
	 *             if the context object is not an instance of {@link MapActivity}.
	 */
	public MyMapView(Context context, MapGenerator mapGenerator) {
		super(context, mapGenerator);
		this.myJobParameters = new JobParameters(MapView.DEFAULT_RENDER_THEME, DEFAULT_TEXT_SCALE);

		//Set new parameters for cache OpenStreetMap
		this.getFileSystemTileCache().setCapacity(200);
		this.getFileSystemTileCache().setPersistent(false);

		//Initialize MapWorker that gets tiles from OpenStreetMap Server
		this.myMapWorker = new MapWorker(this);
		this.myMapWorker.setMapGenerator(mapGenerator);
		this.myMapWorker.start();

		//Initialize cache for OpenSeaMap
		this.inMemoryTileCacheOpenSeaMap = new InMemoryTileCacheOpenSeaMap(DEFAULT_TILE_CACHE_SIZE_IN_MEMORY);
		MapActivity mapActivity = (MapActivity) context;
		this.fileSystemTileCacheOpenSeaMap = new FileSystemTileCacheOpenSeaMap(DEFAULT_TILE_CACHE_SIZE_FILE_SYSTEM,mapActivity.getMapViewId());

		//Initialize MyMapWorker that gets tiles from OpenSeaMap Server
		this.mapWorkerOpenSeaMap = new MyMapWorker(this);
		this.mapWorkerOpenSeaMap.setMapGeneratorOpenSeaMap(OpenSeaMapTileDownloader.getInstance());
		this.mapWorkerOpenSeaMap.start();

		//Initialize overlay that gets tiles from OpenSeaMap Server
		overlayOpenSeaMap = new MyArrayItemizedOverlay(this.getContext(),null);
		this.getOverlays().add(MyMapView.DEFAULT_OVERLAY,overlayOpenSeaMap);

		//Get poi from server
		new GetPoiAsyncTask(this).execute();

		//Get service
		ArrayList<OverlayItem> serviceOverlay = MyXmlParser.getInstance().getService(context);
		overlayOpenSeaMap.initItemsService(serviceOverlay);
		//overlayOpenSeaMap.addItemsService(serviceOverlay);

		this.overlayDraw = new ArrayDrawOverlay();
		this.getOverlays().add(overlayDraw);

		//Initialize zoomCache
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if(mapPosition != null)	zoomCache = mapPosition.zoomLevel;
		else zoomCache = 0;

		if(this.isEnableShowBaliseOSM){
			this.addBaliseMiss();
		}
		//Get map center
		lastMapCenter = getMapPosition().getMapCenter();
	}

	@Override
	public void setMapGenerator(MapGenerator mapGenerator){
		super.setMapGenerator(mapGenerator);
		this.myMapWorker.setMapGenerator(this.getMapGenerator());
		this.getFileSystemTileCache().setPersistent(false);
	}

	/**
	 * Calculates all necessary tiles and adds jobs accordingly.
	 */
	@Override
	public void redrawTiles() {
		if (this.getWidth() <= 0 || this.getHeight() <= 0) {
			return;
		}

		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}

		synchronized( this.getOverlays()) {
			if(mapPosition.zoomLevel != this.zoomCache){

				if(this.isEnableShowBaliseOSM){
					// Clear overlay for OpenSeaMap
					overlayOpenSeaMap.clearOSM();

					addBaliseMiss();
				}		

				if(this.isEnableShowService){
					//Display service
					if(mapPosition.zoomLevel >= 16 && this.zoomCache<16){
						overlayOpenSeaMap.displayService();
					}
					//Hidden service
					else if(mapPosition.zoomLevel < 16){
						this.overlayOpenSeaMap.hiddenService();
					}
				}

				if(this.isEnableShowPoi){
					//Display service
					if(mapPosition.zoomLevel >= 16 && this.zoomCache<16){
						overlayOpenSeaMap.displayPoi();
					}
					//Hidden service
					else if(mapPosition.zoomLevel < 16){
						this.overlayOpenSeaMap.hiddenPoi();
					}
				}

				if(this.isEnableShowText){
					//Display Text
					if(mapPosition.zoomLevel >= 16 && this.zoomCache<16){
						textOverlay = MyXmlParser.getInstance().getText(getContext());
						if(textOverlay != null){
							this.overlayDraw.addTexts(textOverlay);
						}
					}
					//Hidden text
					else if(mapPosition.zoomLevel < 16){
						this.overlayDraw.removeTexts(textOverlay);
					}
				}

				if(this.isEnableShowSounding){
					//Display Sounding
					if(mapPosition.zoomLevel >= 16 && this.zoomCache<16){
						soundingOverlay = MyXmlParser.getInstance().getSounding(getContext());
						if(soundingOverlay != null){
							this.overlayDraw.addTexts(soundingOverlay);
						}
					}
					//Hidden Sounding
					else if(mapPosition.zoomLevel < 16){
						this.overlayDraw.removeTexts(soundingOverlay);
					}
				}


				// Initialize zoomCache
				this.zoomCache = mapPosition.zoomLevel;
			}

			//Request redraw all overlay
			for (int i = 0, n = this.getOverlays().size(); i < n; ++i) {
				this.getOverlays().get(i).requestRedraw();
			}
		}

		// Get pixelLeft and PixelTop of the MyMapView
		GeoPoint geoPoint = mapPosition.geoPoint;
		double pixelLeft = MercatorProjection.longitudeToPixelX(geoPoint.getLongitude(), mapPosition.zoomLevel);
		double pixelTop = MercatorProjection.latitudeToPixelY(geoPoint.getLatitude(), mapPosition.zoomLevel);
		pixelLeft -= getWidth() >> 1;
			pixelTop -= getHeight() >> 1;

					// Get tile for each extremity of the MyMapView
					long tileLeft = MercatorProjection.pixelXToTileX(pixelLeft, mapPosition.zoomLevel);
					long tileTop = MercatorProjection.pixelYToTileY(pixelTop, mapPosition.zoomLevel);
					long tileRight = MercatorProjection.pixelXToTileX(pixelLeft + getWidth(), mapPosition.zoomLevel);
					long tileBottom = MercatorProjection.pixelYToTileY(pixelTop + getHeight(), mapPosition.zoomLevel);

					// Init MapGenerator
					Object cacheId;
					Object cacheIdOpenSeaMap;
					cacheIdOpenSeaMap = OpenSeaMapTileDownloader.getInstance().getHostName();

					if (this.getMapGenerator().requiresInternetConnection()) {
						cacheId = ((TileDownloader) this.getMapGenerator()).getHostName();
					} else {
						cacheId = this.getMapFile();
					}

					// Get job for all tiles we need to display
					for (long tileY = tileTop; tileY <= tileBottom; ++tileY) {
						for (long tileX = tileLeft; tileX <= tileRight; ++tileX) {

							Tile tile = new Tile(tileX, tileY, mapPosition.zoomLevel);

							/******************** Get tile for OpenStreetMap *******************************/
							MapGeneratorJob mapGeneratorJob = new MapGeneratorJob(tile, cacheId, myJobParameters,
									this.getDebugSettings());

							if (this.inMemoryTileCacheOpenStreetMap.containsKey(mapGeneratorJob)) {
								Bitmap bitmap = this.inMemoryTileCacheOpenStreetMap.get(mapGeneratorJob);
								this.getFrameBuffer().drawBitmap(mapGeneratorJob.tile, bitmap);

							} else if (this.getFileSystemTileCache().containsKey(mapGeneratorJob)) {
								Bitmap bitmap = this.getFileSystemTileCache().get(mapGeneratorJob);

								if (bitmap != null) {
									this.getFrameBuffer().drawBitmap(mapGeneratorJob.tile, bitmap);
									this.inMemoryTileCacheOpenStreetMap.put(mapGeneratorJob, bitmap);
								} else {
									// the image data could not be read from the cache
									this.getJobQueue().addJob(mapGeneratorJob);
								}
							} else {
								// cache miss: need to download
								this.getJobQueue().addJob(mapGeneratorJob);
							}

							/******************** Get tile for OpenSeaMap *******************************/
							//Only zoom > 8 have tile
							if(this.isEnableShowBaliseOSM && tile.zoomLevel > 8){
								MapGeneratorJob mapGeneratorJobOpenSeaMap = new MapGeneratorJob(tile, cacheIdOpenSeaMap, myJobParameters,
										this.getDebugSettings());

								if(mapPosition.zoomLevel == tile.zoomLevel && !overlayOpenSeaMap.checkContainsOSM(tile.toString())){		
									if (this.inMemoryTileCacheOpenSeaMap.containsKey(mapGeneratorJobOpenSeaMap)) {
										Bitmap bitmapOpenSeaMap = this.inMemoryTileCacheOpenSeaMap.get(mapGeneratorJobOpenSeaMap);
										OverlayItem myItem = tileToOverlayItem(tile, bitmapOpenSeaMap);
										this.addOverlayOpenSeaMap(myItem);

									} else if (this.fileSystemTileCacheOpenSeaMap.containsKey(mapGeneratorJobOpenSeaMap)) {
										Bitmap bitmapOpenSeaMap = this.fileSystemTileCacheOpenSeaMap.get(mapGeneratorJobOpenSeaMap);

										if (bitmapOpenSeaMap != null) {
											Bitmap myBitmap = Bitmap.createBitmap(bitmapOpenSeaMap);
											OverlayItem myItem = tileToOverlayItem(tile, myBitmap);
											this.addOverlayOpenSeaMap(myItem);
											this.inMemoryTileCacheOpenSeaMap.put(mapGeneratorJobOpenSeaMap, myBitmap);
										} else{
											// the image data could not be read from the cache
											if (this.getMapGenerator().requiresInternetConnection()){
												this.jobQueueOpenSeaMap.addJob(mapGeneratorJobOpenSeaMap);
											}		
										}
									} else {
										if (this.getMapGenerator().requiresInternetConnection()){
											//Get the last version
											this.jobQueueOpenSeaMap.addJob(mapGeneratorJobOpenSeaMap);
										}
										else{
											//Get in cache directory
											Bitmap bitmapOpenSeaMap = this.fileSystemTileCacheOpenSeaMap.get(mapGeneratorJobOpenSeaMap,true);
											if (bitmapOpenSeaMap != null) {
												Bitmap myBitmap = Bitmap.createBitmap(bitmapOpenSeaMap);
												OverlayItem myItem = tileToOverlayItem(tile, myBitmap);
												this.addOverlayOpenSeaMap(myItem);
												this.inMemoryTileCacheOpenSeaMap.put(mapGeneratorJobOpenSeaMap, myBitmap);
												this.fileSystemTileCacheOpenSeaMap.put(mapGeneratorJobOpenSeaMap, myBitmap);
											}
										}
									}
								}
							}
						}
					}

					if (this.getMapScaleBar().isShowMapScaleBar()) {
						this.getMapScaleBar().redrawScaleBar();
					}

					invalidateOnUiThread();

					// Start new job for download tiles for OpenStreetMap
					this.getJobQueue().requestSchedule();
					synchronized (this.myMapWorker) {
						this.myMapWorker.notify();
					}

					// Start new job for download tiles for OpenSeaMap
					this.jobQueueOpenSeaMap.requestSchedule();
					synchronized (this.mapWorkerOpenSeaMap) {
						this.mapWorkerOpenSeaMap.notify();
					}
	}

	/** 
	 * @param item
	 *            tile from overlaySeaMap to display on the map
	 */
	public void addItem(OverlayItem item){
		overlayOpenSeaMap.addItem(item,true);
	}

	/** 
	 * @param item
	 *            circle to display on the map
	 */
	public void addCircle(OverlayCircle circle){
		this.overlayDraw.addCircle(circle);
	}

	public void updatePosition(){
		this.overlayOpenSeaMap.requestRedraw();
		this.overlayDraw.requestRedraw();
	}

	/**
	 * @param item
	 * 			the {@link OverlayItem} to add in the {@link ArrayItemizedOverlay} overlayOpenSeaMap
	 */
	public void addOverlayOpenSeaMap(OverlayItem item){
		overlayOpenSeaMap.addItemOSM(item);
	}

	/**
	 * @param tile
	 *            the {@link Tile} to display.
	 * @param tileBitmap
	 *            the {@link Bitmap} of the tile.
	 * @return an {@link OverlayItem} that contains a marker to draw the tile at the correct position (may be null)
	 */
	public final OverlayItem tileToOverlayItem(Tile tile,Bitmap tileBitmap){
		// Get my position
		MapPosition mapPosition = this.getMapPosition().getMapPosition();

		if (tile.zoomLevel != mapPosition.zoomLevel) {
			// the tile doesn't fit to the current zoom level
			return null;
		} else if (this.isZoomAnimatorRunning()) {
			// do not disturb the ongoing animation
			return null;
		}

		// Get pixelLeft and PixelTop of the MyMapView
		GeoPoint maPositionGeo = mapPosition.geoPoint;
		double pixelLeft = MercatorProjection.longitudeToPixelX(maPositionGeo.getLongitude(), mapPosition.zoomLevel);
		double pixelTop = MercatorProjection.latitudeToPixelY(maPositionGeo.getLatitude(), mapPosition.zoomLevel);
		pixelLeft -= this.getWidth() >> 1;
					pixelTop -= this.getHeight() >> 1;

					// Get the correct position to display the tile
					int left = (int) (tile.getPixelX() - pixelLeft +256/2);
					int top =  (int) (tile.getPixelY() - pixelTop+256);
					GeoPoint geoPointTile = this.getProjection().fromPixels(left, top);

					// Create a item that contains the tile
					Drawable tileMarker = new BitmapDrawable(getResources(),tileBitmap);
					ItemizedOverlay.boundCenterBottom(tileMarker);
					OverlayItem item = new OverlayItem();
					item.setPoint(geoPointTile);
					item.setSnippet("noTap");
					item.setTitle(tile.toString());
					item.setMarker(tileMarker);

					return item;
	}

	public JobQueue getJobQueueOpenSeaMap(){
		return this.jobQueueOpenSeaMap;
	}

	@Override
	public InMemoryTileCache getInMemoryTileCache(){
		//Because it is use on super()
		if(inMemoryTileCacheOpenStreetMap == null){
			inMemoryTileCacheOpenStreetMap = new InMemoryTileCache(50);
		}
		return this.inMemoryTileCacheOpenStreetMap;
	}

	public InMemoryTileCacheOpenSeaMap getInMemoryTileCacheOpenSeaMap(){
		return this.inMemoryTileCacheOpenSeaMap;
	}

	public FileSystemTileCacheOpenSeaMap getFileSystemTileCacheOpenSeaMap(){
		return this.fileSystemTileCacheOpenSeaMap;
	}

	public void showBaliseOSM(){		
		this.isEnableShowBaliseOSM = true;
		addBaliseMiss();

		this.overlayOpenSeaMap.displayOSM();
	}

	public void hiddenBalise(){
		this.isEnableShowBaliseOSM = false;
		this.overlayOpenSeaMap.hiddenOSM();
	}

	public void showService(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}

		this.isEnableShowService = true;
		if(mapPosition.zoomLevel >= 16){
			this.overlayOpenSeaMap.displayService();
		}
	}

	public void hiddenService(){
		this.isEnableShowService = false;
		this.overlayOpenSeaMap.hiddenService();
	}

	public void showPoi(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}

		this.isEnableShowPoi = true;
		if(mapPosition.zoomLevel >= 16){
			this.overlayOpenSeaMap.displayPoi();
		}
	}

	public void hiddenPoi(){
		this.isEnableShowPoi = false;
		this.overlayOpenSeaMap.hiddenPoi();
	}

	public void showText(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}
		textOverlay = MyXmlParser.getInstance().getText(getContext());

		if(textOverlay != null){
			this.isEnableShowText = true;
			if(mapPosition.zoomLevel >= 16){
				this.overlayDraw.addTexts(textOverlay);
			}
		}
	}

	public void hiddenText(){
		this.isEnableShowText = false;
		this.overlayDraw.removeTexts(textOverlay);
	}

	public void showSounding(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}
		soundingOverlay = MyXmlParser.getInstance().getSounding(getContext());

		if(soundingOverlay != null){
			this.isEnableShowSounding = true;
			if(mapPosition.zoomLevel >= 16){
				this.overlayDraw.addTexts(soundingOverlay);
			}
		}
	}

	public void hiddenSounding(){
		this.isEnableShowSounding = false;
		this.overlayDraw.removeTexts(soundingOverlay);
	}

	private void addBaliseMiss(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}
		if(mapPosition.zoomLevel < 16) return;

		//Add balise missing on the map
		GeoPoint positionBalise = new GeoPoint(48.3772,-4.4953);
		Drawable baliseIcon = this.getContext().getResources().getDrawable(R.drawable.balise);
		OverlayItem baliseItem = new OverlayItem(positionBalise,"","noTap",ItemizedOverlay.boundCenter(baliseIcon));
		overlayOpenSeaMap.addItemOSM(baliseItem);
	}

	/**
	 * Convert poiList to an ArrayList<OverlayItem>
	 * @param poiList
	 *           {@link PoisDTOList} to display.
	 */
	public void setPoi(PoisDTOList poiList){
		ArrayList<OverlayItem> poiOverlay = new ArrayList<OverlayItem>();
		for(int i=0; i<poiList.size(); i++){
			PoiDTO poi = poiList.get(i);

			OverlayItem item = new OverlayItem();

			double latitude = Double.parseDouble(poi.getLatitude());
			double longitude = Double.parseDouble(poi.getLongitude());

			int idDrawable = R.drawable.bon_plan;
			String type = poi.getType();

			if(type.equals("peche")){
				idDrawable = R.drawable.poisson;
			}
			else if(type.equals("securite")){
				idDrawable = R.drawable.attention;
			}

			item.setMarker(ItemizedOverlay.boundCenter(getContext().getResources().getDrawable(idDrawable)));
			//save id in snippet
			item.setSnippet(poi.getId().toString());
			item.setTitle(type);
			item.setPoint(new GeoPoint(latitude,longitude));
			//Get information from server
			new GetPoiInfoAsyncTask(item).execute(poi.getId().toString());
			poiOverlay.add(item);
		}
		overlayOpenSeaMap.initItemsPoi(poiOverlay);
	}

	@Override
	void onPause() {
		super.onPause();
		this.myMapWorker.pause();
		this.mapWorkerOpenSeaMap.pause();
	}

	@Override
	public void onResume() {
		super.onResume();
		this.myMapWorker.proceed();
		this.mapWorkerOpenSeaMap.proceed();
	}

	final Handler handler = new Handler(); 
	Runnable mLongPressed = new Runnable() {
		public void run() {
			GeoPoint longPressPoint = getProjection().fromPixels((int) previousPositionX,
					(int) previousPositionY);
			double lat = longPressPoint.getLatitude();
			double lon = longPressPoint.getLongitude(); 
			// Create an instance of the dialog fragment and show it
			AddPoiDialog dialog = AddPoiDialog.getInstance(lat,lon);

			Activity activity = (Activity) getContext();
			dialog.show(activity.getFragmentManager(), "AddCommentDialog");
		}   
	};

	@Override
	void destroy() {
		this.myMapWorker.interrupt();
		this.mapWorkerOpenSeaMap.interrupt();
		try {
			this.myMapWorker.join();
			this.mapWorkerOpenSeaMap.join();
		} catch (InterruptedException e) {
			// restore the interrupted status
			Thread.currentThread().interrupt();
		}
		this.inMemoryTileCacheOpenStreetMap.destroy();
		this.inMemoryTileCacheOpenSeaMap.destroy();
		this.fileSystemTileCacheOpenSeaMap.destroy();
		super.destroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		boolean result = super.onTouchEvent(event);

		switch(event.getAction()){

		case MotionEvent.ACTION_DOWN:
			int index = event.getActionIndex();
			int pointerId = event.getPointerId(index);
			int pointerIndex = event.findPointerIndex(pointerId);
			// save the position of the event
			this.previousPositionX = event.getX(pointerIndex);
			this.previousPositionY = event.getY(pointerIndex);
			//Time enough long to avoid calling handler if another OnLongPress was handled in super.onTouchEvent(event)
			handler.postDelayed(mLongPressed, 1000);
			return true;

		case MotionEvent.ACTION_MOVE :
			if (!getMapPosition().getMapCenter().equals(lastMapCenter)) {
				// User is panning the map, this is no longpress
				handler.removeCallbacks(mLongPressed);
			}

			lastMapCenter = getMapPosition().getMapCenter();
			return true;

		case MotionEvent.ACTION_UP :
			handler.removeCallbacks(mLongPressed);
			return true;

		}
		//If multitouch
		if(event.getPointerCount() > 1){
			handler.removeCallbacks(mLongPressed);
		}
		return result;
	}

	public void addPoi(OverlayItem item) {
		this.overlayOpenSeaMap.addItemPoi(item);
	}
}