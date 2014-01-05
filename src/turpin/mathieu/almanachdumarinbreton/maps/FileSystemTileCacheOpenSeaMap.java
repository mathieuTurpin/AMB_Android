/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package turpin.mathieu.almanachdumarinbreton.maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mapsforge.android.AndroidUtils;
import org.mapsforge.android.maps.mapgenerator.FileSystemTileCache;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.core.IOUtils;
import org.mapsforge.core.Tile;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Environment;

/**
 * A thread-safe cache for image files with a variable size and LRU policy.
 */
public class FileSystemTileCacheOpenSeaMap extends FileSystemTileCache {
	private static final class ImageFileNameFilter implements FilenameFilter {
		static final FilenameFilter INSTANCE = new ImageFileNameFilter();

		private ImageFileNameFilter() {
			// do nothing
		}

		@Override
		public boolean accept(File directory, String fileName) {
			return fileName.endsWith(IMAGE_FILE_NAME_EXTENSION);
		}
	}
	
	/**
	 * Path to the caching folder on the external storage.
	 */
	private static final String CACHE_DIRECTORY = "/Android/data/org.mapsforge.android.maps/cacheOpenSeaMap/";

	/**
	 * File name extension for cached images.
	 */
	private static final String IMAGE_FILE_NAME_EXTENSION = ".tile";

	/**
	 * Load factor of the internal HashMap.
	 */
	private static final float LOAD_FACTOR = 0.6f;

	private static final Logger LOG = Logger.getLogger(FileSystemTileCacheOpenSeaMap.class.getName());

	/**
	 * Name of the file used for serialization of the cache map.
	 */
	private static final String SERIALIZATION_FILE_NAME = "cache.ser";

	private static File createDirectory(String pathName) {
		File file = new File(pathName);
		if (!file.exists() && !file.mkdirs()) {
			throw new IllegalArgumentException("could not create directory: " + file);
		} else if (!file.isDirectory()) {
			throw new IllegalArgumentException("not a directory: " + file);
		} else if (!file.canRead()) {
			throw new IllegalArgumentException("cannot read directory: " + file);
		} else if (!file.canWrite()) {
			throw new IllegalArgumentException("cannot write directory: " + file);
		}
		return file;
	}
		
	private static Map<MapGeneratorJob, File> createMapOpenSeaMap(final int mapCapacity) {
		int initialCapacity = (int) (mapCapacity / LOAD_FACTOR) + 2;

		return new LinkedHashMap<MapGeneratorJob, File>(initialCapacity, LOAD_FACTOR, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<MapGeneratorJob, File> eldestEntry) {
				if (size() > mapCapacity) {
					remove(eldestEntry.getKey());
					if (!eldestEntry.getValue().delete()) {
						eldestEntry.getValue().deleteOnExit();
					}
				}
				return false;
			}
		};
	}

	/**
	 * Restores the serialized cache map if possible.
	 * 
	 * @param directory
	 *            the directory of the serialized map file.
	 * @return the deserialized map or null, in case of an error.
	 */
	private static Map<MapGeneratorJob, File> deserializeMap(File directory) {
		File serializedMapFile = new File(directory, SERIALIZATION_FILE_NAME);
		if (!serializedMapFile.exists() || !serializedMapFile.isFile() || !serializedMapFile.canRead()) {
			return null;
		}

		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			fileInputStream = new FileInputStream(serializedMapFile);
			objectInputStream = new ObjectInputStream(fileInputStream);

			// the compiler warning in the following line cannot be avoided unfortunately
			Map<MapGeneratorJob, File> map = (Map<MapGeneratorJob, File>) objectInputStream.readObject();

			if (!serializedMapFile.delete()) {
				serializedMapFile.deleteOnExit();
			}

			return map;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, null, e);
			return null;
		} catch (ClassNotFoundException e) {
			LOG.log(Level.SEVERE, null, e);
			return null;
		} finally {
			IOUtils.closeQuietly(objectInputStream);
			IOUtils.closeQuietly(fileInputStream);
		}
	}

	private static int getCapacity(int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException("capacity must not be negative: " + capacity);
		} else if (AndroidUtils.applicationRunsOnAndroidEmulator()) {
			return 0;
		}
		return capacity;
	}
	
	/**
	 * Serializes the cache map.
	 * 
	 * @param directory
	 *            the directory of the serialized map file.
	 * @param map
	 *            the map to be serialized.
	 * @return true if the map was serialized successfully, false otherwise.
	 */
	private static boolean serializeMap(File directory, Map<MapGeneratorJob, File> map) {
		File serializedMapFile = new File(directory, SERIALIZATION_FILE_NAME);
		if (serializedMapFile.exists() && !serializedMapFile.delete()) {
			return false;
		}

		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(serializedMapFile);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(map);
			return true;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, null, e);
			return false;
		} finally {
			IOUtils.closeQuietly(objectOutputStream);
			IOUtils.closeQuietly(fileOutputStream);
		}
	}

	private final Bitmap myBitmapGet;
	private final ByteBuffer myByteBuffer;
	private final File cacheDirectoryOpenSeaMap;
	//private long cacheId;
	private Map<MapGeneratorJob, File> myMap;
	private boolean persistent;
	
	private final String rootDirectory;

	/**
	 * @param capacity
	 *            the maximum number of entries in this cache.
	 * @param mapViewId
	 *            the ID of the MapView to separate caches for different MapViews.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	public FileSystemTileCacheOpenSeaMap(int capacity, int mapViewId) {
		super(capacity,mapViewId);
	
		String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		String cacheDirectoryPath = externalStorageDirectory + CACHE_DIRECTORY;
		rootDirectory = cacheDirectoryPath;
		this.cacheDirectoryOpenSeaMap = createDirectory(cacheDirectoryPath);

		Map<MapGeneratorJob, File> deserializedMap = deserializeMap(this.cacheDirectoryOpenSeaMap);
		if (deserializedMap == null) {
			this.myMap = createMapOpenSeaMap(this.getCapacity());
		} else {
			this.myMap = deserializedMap;
		}
		this.myByteBuffer = ByteBuffer.allocate(4*Tile.TILE_SIZE*Tile.TILE_SIZE);
		this.myBitmapGet = Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE, Config.ARGB_8888);
	}

	@Override
	public synchronized boolean containsKey(MapGeneratorJob mapGeneratorJob) {
		return this.myMap.containsKey(mapGeneratorJob);
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		if (!this.persistent || !serializeMap(this.cacheDirectoryOpenSeaMap, this.myMap)) {
			for (File file : this.myMap.values()) {
				if (!file.delete()) {
					file.deleteOnExit();
				}
			}
			this.myMap.clear();

			File[] filesToDelete = this.cacheDirectoryOpenSeaMap.listFiles(ImageFileNameFilter.INSTANCE);
			if (filesToDelete != null) {
				for (File file : filesToDelete) {
					if (!file.delete()) {
						file.deleteOnExit();
					}
				}
			}

			if (!this.cacheDirectoryOpenSeaMap.delete()) {
				this.cacheDirectoryOpenSeaMap.deleteOnExit();
			}
		}
	}
	
	@Override
	public synchronized Bitmap get(MapGeneratorJob mapGeneratorJob){
		return get(mapGeneratorJob,false);
	}

	public synchronized Bitmap get(MapGeneratorJob mapGeneratorJob,boolean searchInCache) {
		if (this.getCapacity() == 0) {
			return null;
		}

		FileInputStream fileInputStream = null;
		try {
			File inputFile;
			if(searchInCache){
				inputFile = getFileCache(mapGeneratorJob);
			}
			else{
				inputFile = this.myMap.get(mapGeneratorJob);
			}

			fileInputStream = new FileInputStream(inputFile);
			byte[] array = this.myByteBuffer.array();
			int bytesRead = fileInputStream.read(array);

			if (bytesRead == array.length) {
				this.myByteBuffer.rewind();
				this.myBitmapGet.copyPixelsFromBuffer(this.myByteBuffer);
				return this.myBitmapGet;
			}

			return null;
		} catch (FileNotFoundException e) {
			this.myMap.remove(mapGeneratorJob);
			return null;
		} catch (IOException e) {
			LOG.log(Level.SEVERE, null, e);
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				LOG.log(Level.SEVERE, null, e);
			}
		}
	}

	@Override
	public synchronized void put(MapGeneratorJob mapGeneratorJob, Bitmap bitmap) {
		if (this.getCapacity() == 0) {
			return;
		}

		FileOutputStream fileOutputStream = null;
		try {
			File outputFile;
			
			/*do {
				++this.cacheId;
				outputFile = new File(this.cacheDirectoryOpenSeaMap, this.cacheId + IMAGE_FILE_NAME_EXTENSION);
			} while (outputFile.exists());*/
			
			outputFile = getFileCache(mapGeneratorJob);
			
			this.myByteBuffer.rewind();
			bitmap.copyPixelsToBuffer(this.myByteBuffer);
			byte[] array = this.myByteBuffer.array();

			fileOutputStream = new FileOutputStream(outputFile);
			fileOutputStream.write(array, 0, array.length);

			this.myMap.put(mapGeneratorJob, outputFile);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, null, e);
		} finally {
			try {
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				LOG.log(Level.SEVERE, null, e);
			}
		}
	}

	private File getFileCache(MapGeneratorJob mapGeneratorJob){
		String tileString = "" + mapGeneratorJob.tile.zoomLevel + "/" + mapGeneratorJob.tile.tileX + "/";
		File folder = createDirectory(rootDirectory + tileString);
		return new File(folder, mapGeneratorJob.tile.tileY + IMAGE_FILE_NAME_EXTENSION);
	}

	@Override
	public synchronized void setCapacity(int capacity) {
		if (this.getCapacity() == capacity) {
			return;
		}

		Map<MapGeneratorJob, File> newMap = createMapOpenSeaMap(getCapacity(capacity));
		newMap.putAll(this.myMap);
		this.myMap = newMap;
	}

}
