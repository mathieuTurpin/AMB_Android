package org.mapsforge.android.maps;

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

import turpin.mathieu.almanachdumarinbreton.MyXmlParser;
import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.maps.FileSystemTileCacheOpenSeaMap;
import turpin.mathieu.almanachdumarinbreton.maps.InMemoryTileCacheOpenSeaMap;
import turpin.mathieu.almanachdumarinbreton.maps.MyMapWorker;
import turpin.mathieu.almanachdumarinbreton.maps.OpenSeaMapTileDownloader;
import turpin.mathieu.almanachdumarinbreton.overlay.ArrayDrawOverlay;
import turpin.mathieu.almanachdumarinbreton.overlay.ArrayTextOverlay;
import turpin.mathieu.almanachdumarinbreton.overlay.MyArrayItemizedOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

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
public class MyMapView extends MapView {
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
	private final InMemoryTileCache inMemoryTileCacheOpenStreetMap;

	//Cache for OpenSeaMap
	private final InMemoryTileCacheOpenSeaMap inMemoryTileCacheOpenSeaMap;
	private final FileSystemTileCacheOpenSeaMap fileSystemTileCacheOpenSeaMap;
	
	//MyMapWorker that controls download tiles from OpenSeaMap Server
	private final MyMapWorker mapWorkerOpenSeaMap;
	
	//ArrayItemizedOverlay that gets overlay make with tiles from OpenSeaMap Server for the current zoom
	private final MyArrayItemizedOverlay overlayOpenSeaMap;	
	private final ArrayDrawOverlay overlayDraw;
	
	private byte zoomCache;
	private boolean isEnableShowBaliseOSM;

	private boolean isEnableShowService;

	private MyXmlParser xmlParser;
	private ArrayTextOverlay textOverlay;
	private boolean isEnableShowText;
	private ArrayTextOverlay soundingOverlay;
	private boolean isEnableShowSounding;


	/**
	 * Need to test
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
		xmlParser = new MyXmlParser(context);
		this.myJobParameters = new JobParameters(MapView.DEFAULT_RENDER_THEME, DEFAULT_TEXT_SCALE);
		
		//Initialize cache for OpenStreetMap
		this.inMemoryTileCacheOpenStreetMap = new InMemoryTileCache(50);
		this.getFileSystemTileCache().setCapacity(200);
		
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
		
		//Get service
		ArrayList<OverlayItem> serviceOverlay = xmlParser.getService();
		overlayOpenSeaMap.addItemsService(serviceOverlay);
		
		this.overlayDraw = new ArrayDrawOverlay();
		this.getOverlays().add(overlayDraw);
		
		//Initialize zoomCache
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		zoomCache = mapPosition.zoomLevel;
		
		if(mapPosition.zoomLevel >=16){
			this.addBaliseMiss();
		}
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
					
					if(mapPosition.zoomLevel >= 16){
						addBaliseMiss();
					}
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
				
				if(this.isEnableShowText){
					//Display Text
					if(mapPosition.zoomLevel >= 16 && this.zoomCache<16){
						if(!this.getOverlays().contains(textOverlay)){
							textOverlay = this.xmlParser.getText();
							if(textOverlay != null){
								this.getOverlays().add(textOverlay);
							}
						}
					}
					//Hidden text
					else if(mapPosition.zoomLevel < 16){
						this.getOverlays().remove(textOverlay);
					}
				}
				
				if(this.isEnableShowSounding){
					//Display Text
					if(mapPosition.zoomLevel >= 16 && this.zoomCache<16){
						soundingOverlay = this.xmlParser.getSounding();
						if(soundingOverlay != null){
							this.getOverlays().add(soundingOverlay);
						}
					}
					//Hidden text
					else if(mapPosition.zoomLevel < 16){
						this.getOverlays().remove(soundingOverlay);
					}
				}
				
				
				// Initialize zoomCache
				this.zoomCache = mapPosition.zoomLevel;
			}
			
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
		if (this.getMapGenerator().requiresInternetConnection()) {
			cacheId = ((TileDownloader) this.getMapGenerator()).getHostName();
			cacheIdOpenSeaMap = OpenSeaMapTileDownloader.getInstance().getHostName();
		} else {
			cacheId = this.getMapFile();
			// Need code here for offline
			cacheIdOpenSeaMap = null;
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
							} else {
								// the image data could not be read from the cache
								this.jobQueueOpenSeaMap.addJob(mapGeneratorJobOpenSeaMap);
							}
						} else {
							// cache miss: need to download
							this.jobQueueOpenSeaMap.addJob(mapGeneratorJobOpenSeaMap);
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
	
	public void addItem(OverlayItem item){
		overlayOpenSeaMap.addItem(item,true);
	}
	
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
	
	public void showText(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}
		textOverlay = this.xmlParser.getText();
		
		if(textOverlay != null){
			this.isEnableShowText = true;
			if(mapPosition.zoomLevel >= 16){
				this.getOverlays().add(textOverlay);
			}
		}
	}
	
	public void hiddenText(){
		this.isEnableShowText = false;
		this.getOverlays().remove(textOverlay);
	}
	
	public void showSounding(){
		MapPosition mapPosition = this.getMapPosition().getMapPosition();
		if (mapPosition == null) {
			return;
		}
		soundingOverlay = this.xmlParser.getSounding();
		
		if(soundingOverlay != null){
			this.isEnableShowSounding = true;
			if(mapPosition.zoomLevel >= 16){
				this.getOverlays().add(soundingOverlay);
			}
		}
	}
	
	public void hiddenSounding(){
		this.isEnableShowSounding = false;
		this.getOverlays().remove(soundingOverlay);
	}
	
	private void addBaliseMiss(){
		//Add balise manquante sur la carte
		GeoPoint positionBalise = new GeoPoint(48.3772,-4.4953);
		Drawable baliseIcon = this.getContext().getResources().getDrawable(R.drawable.balise);
		OverlayItem baliseItem = new OverlayItem(positionBalise,"","tileOSM",ItemizedOverlay.boundCenter(baliseIcon));
		overlayOpenSeaMap.addItemOSM(baliseItem);
	}
}
