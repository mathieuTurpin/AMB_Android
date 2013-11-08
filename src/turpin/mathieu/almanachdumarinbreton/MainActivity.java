package turpin.mathieu.almanachdumarinbreton;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MyMapView;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.MapnikTileDownloader;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class MainActivity extends MapActivity {
	public MyMapView mapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Create map with tiles from OpenStreetMap and make overlays with tiles from OpenSeaMap
		mapView = new MyMapView(this, new MapnikTileDownloader());
        mapView.setClickable(true);
        
        //Displays ZoomControls on the map
        mapView.setBuiltInZoomControls(true);
        
        //Display scale on the map
        mapView.getMapScaleBar().setShowMapScaleBar(true);
        setContentView(mapView);
        
 	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return true;
	}
}
