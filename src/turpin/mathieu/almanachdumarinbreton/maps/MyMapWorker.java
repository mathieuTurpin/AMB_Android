package turpin.mathieu.almanachdumarinbreton.maps;

import org.mapsforge.android.maps.MyMapView;
import org.mapsforge.android.maps.mapgenerator.JobQueue;
import org.mapsforge.android.maps.mapgenerator.MapGenerator;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.android.maps.mapgenerator.MapWorker;
//import org.mapsforge.android.maps.mapgenerator.TileCache;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.Tile;

import android.graphics.Bitmap;

/**
 * A MyMapWorker uses a {@link MapGenerator} to generate map tiles. It runs in a separate thread to avoid blocking the UI
 * thread.
 */
public class MyMapWorker extends MapWorker{
	
	//private final TileCache fileSystemTileCacheOpenSeaMap;
	//private final TileCache inMemoryTileCacheOpenSeaMap;
	private final JobQueue jobQueueOpenSeaMap;
	private MapGenerator mapGeneratorOpenSeaMap;
	private Bitmap tileBitmap;
	private final MyMapView mapView;
	
	/**
	 * @param mapView
	 *            the {@link MyMapView} for which this {@link MyMapWorker} generates map tiles.
	 */
	public MyMapWorker(MyMapView mapView) {
		super(mapView);

		this.jobQueueOpenSeaMap = mapView.getJobQueueOpenSeaMap();
		this.mapView = mapView;
		//this.inMemoryTileCacheOpenSeaMap = mapView.getInMemoryTileCacheOpenSeaMap();
		//this.fileSystemTileCacheOpenSeaMap = mapView.getFileSystemTileCacheOpenSeaMap();
	}
	
	@Override
	protected boolean hasWork() {
		return !this.jobQueueOpenSeaMap.isEmpty();
	}
	
	@Override
	protected void doWork() {
		this.tileBitmap = Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE, Bitmap.Config.ARGB_8888);
		MapGeneratorJob mapGeneratorJob = this.jobQueueOpenSeaMap.poll();

		/*
		 * Need to improve for cache tiles for OpenSeaMap
			if (this.inMemoryTileCacheOpenSeaMap.containsKey(mapGeneratorJob)) {
				return;
			} else if (this.fileSystemTileCacheOpenSeaMap.containsKey(mapGeneratorJob)) {
				return;
			}
		*/

		boolean success = this.mapGeneratorOpenSeaMap.executeJob(mapGeneratorJob, this.tileBitmap);

		if (!isInterrupted() && success) {
			OverlayItem item = this.mapView.tileToOverlayItem(mapGeneratorJob.tile, this.tileBitmap);
			this.mapView.addOverlayOpenSeaMap(item);
			/*
			 * Need to improve for cache tiles for OpenSeaMap
				//this.inMemoryTileCacheOpenSeaMap.put(mapGeneratorJob, this.tileBitmap);
				//this.fileSystemTileCacheOpenSeaMap.put(mapGeneratorJob, this.tileBitmap);
			*/
		}
	}

	/**
	 * @param mapGenerator
	 *            the {@link MapGenerator} which this {@link MyMapWorker} should use.
	 */
	public void setMapGeneratorOpenSeaMap(MapGenerator mapGenerator) {
		this.mapGeneratorOpenSeaMap = mapGenerator;
	}

}