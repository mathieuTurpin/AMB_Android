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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mapsforge.android.maps.mapgenerator.InMemoryTileCache;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.core.Tile;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

/**
 * A thread-safe cache for tile images with a fixed size and LRU policy.
 */
public class InMemoryTileCacheOpenSeaMap extends InMemoryTileCache {
	/**
	 * Load factor of the internal HashMap.
	 */
	private static final float LOAD_FACTOR = 0.6f;

	//CreateBitmapPool to Tile with Config.ARGB_8888
	private static List<Bitmap> createBitmapPoolOpenSeaMap(int poolSize) {
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();

		for (int i = 0; i < poolSize; ++i) {
			Bitmap bitmap = Bitmap.createBitmap(Tile.TILE_SIZE, Tile.TILE_SIZE, Config.ARGB_8888);
			bitmaps.add(bitmap);
		}

		return bitmaps;
	}

	private static Map<MapGeneratorJob, Bitmap> createMapOpenSeaMap(final int mapCapacity, final List<Bitmap> bitmapPool) {
		int initialCapacity = (int) (mapCapacity / LOAD_FACTOR) + 2;

		return new LinkedHashMap<MapGeneratorJob, Bitmap>(initialCapacity, LOAD_FACTOR, true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<MapGeneratorJob, Bitmap> eldestEntry) {
				if (size() > mapCapacity) {
					remove(eldestEntry.getKey());
					bitmapPool.add(eldestEntry.getValue());
				}
				return false;
			}
		};
	}

	private final List<Bitmap> myBitmapPool;
	private final ByteBuffer myByteBuffer;
	private final Map<MapGeneratorJob, Bitmap> myMap;

	/**
	 * @param capacity
	 *            the maximum number of entries in this cache.
	 * @throws IllegalArgumentException
	 *             if the capacity is negative.
	 */
	public InMemoryTileCacheOpenSeaMap(int capacity) {
		super(capacity);
		this.myBitmapPool = createBitmapPoolOpenSeaMap(this.getCapacity() + 1);
		this.myMap = createMapOpenSeaMap(this.getCapacity(), this.myBitmapPool);
		this.myByteBuffer = ByteBuffer.allocate(4*Tile.TILE_SIZE*Tile.TILE_SIZE);
	}

	@Override
	public boolean containsKey(MapGeneratorJob mapGeneratorJob) {
		synchronized (this.myMap) {
			return this.myMap.containsKey(mapGeneratorJob);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		synchronized (this.myMap) {
			for (Bitmap bitmap : this.myMap.values()) {
				bitmap.recycle();
			}
			this.myMap.clear();

			for (Bitmap bitmap : this.myBitmapPool) {
				bitmap.recycle();
			}
			this.myBitmapPool.clear();
		}
	}

	@Override
	public Bitmap get(MapGeneratorJob mapGeneratorJob) {
		synchronized (this.myMap) {
			return this.myMap.get(mapGeneratorJob);
		}
	}

	@Override
	public void put(MapGeneratorJob mapGeneratorJob, Bitmap bitmap) {
		if (this.getCapacity() == 0) {
			return;
		}

		synchronized (this.myMap) {
			if (this.myBitmapPool.isEmpty()) {
				return;
			}
			
			Bitmap pooledBitmap = this.myBitmapPool.remove(this.myBitmapPool.size() - 1);
			this.myByteBuffer.rewind();
			bitmap.copyPixelsToBuffer(this.myByteBuffer);
			this.myByteBuffer.rewind();
			pooledBitmap.copyPixelsFromBuffer(this.myByteBuffer);
			this.myMap.put(mapGeneratorJob, pooledBitmap);
		}
	}

}
