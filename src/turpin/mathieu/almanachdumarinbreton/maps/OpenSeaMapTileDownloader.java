package turpin.mathieu.almanachdumarinbreton.maps;

import org.mapsforge.android.maps.mapgenerator.tiledownloader.TileDownloader;
import org.mapsforge.core.Tile;

/**
 * Singleton
 * A {@link MapGenerator} that downloads tiles for OpenSeaMap from the server at OpenSeaMap.
 */
public class OpenSeaMapTileDownloader extends TileDownloader {
	private static OpenSeaMapTileDownloader singleton = null;
	
	private static final String HOST_NAME = "tiles.openseamap.org";
	private static final String PROTOCOL = "http";
	private static final byte ZOOM_MAX = 18;
	
	private final StringBuilder stringBuilder;

	/**
	 * Constructs a new OpenSeaMapTileDownloader.
	 */
	private OpenSeaMapTileDownloader() {
		super();
		this.stringBuilder = new StringBuilder();
	}

	/**
	 * @return an instance of {@link OpenSeaMapTileDonwloader} and create it if it does not exist
	 */
	public static synchronized OpenSeaMapTileDownloader getInstance(){
		if(singleton == null) singleton = new OpenSeaMapTileDownloader();
		return singleton;
	}
	
	/**
	 * @param tile
	 * 		  the {@link Tile} to download
	 * @return path to download the tile
	 */
	@Override
	public String getTilePath(Tile tile) {		
		this.stringBuilder.setLength(0);
		this.stringBuilder.append("/seamark/");
		this.stringBuilder.append(tile.zoomLevel);
		this.stringBuilder.append('/');
		this.stringBuilder.append(tile.tileX);
		this.stringBuilder.append('/');
		this.stringBuilder.append(tile.tileY);
		this.stringBuilder.append(".png");

		return this.stringBuilder.toString();
	}
	
	@Override
	public String getHostName() {
		return HOST_NAME;
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}
	
	@Override
	public byte getZoomLevelMax() {
		return ZOOM_MAX;
	}
}
