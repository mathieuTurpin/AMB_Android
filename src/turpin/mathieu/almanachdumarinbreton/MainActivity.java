package turpin.mathieu.almanachdumarinbreton;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MyMapView;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.MapnikTileDownloader;
import org.mapsforge.android.maps.overlay.ArrayCircleOverlay;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.android.maps.overlay.OverlayItem;

import turpin.mathieu.almanachdumarinbreton.maps.MyItemizedOverlay;
import turpin.mathieu.almanachdumarinbreton.maps.MyXmlParser;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
//import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends MapActivity{
	private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;

	public MyMapView mapView;
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;
	public MapController mapController;
	
	ArrayCircleOverlay circleOverlay;
	ArrayItemizedOverlay itemizedOverlay;
	OverlayCircle overlayCircle;
	OverlayItem overlayItem;
	private Paint circleOverlayFill;
	private Paint circleOverlayOutline;

	//Sensor for orientation
	//private SensorManager sensorManager;
	//MySensorListener mySensorListener;
	private boolean snapToLocation;

	TextView infoPosition;
	TextView infoSpeed;
	TextView infoBearing;
	
	MyItemizedOverlay baliseOverlay;

	private ToggleButton snapToLocationView;

	private MyXmlParser xmlParser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoPosition = (TextView) findViewById(R.id.infoPosition);
		infoSpeed = (TextView) findViewById(R.id.infoSpeed);
		infoBearing = (TextView) findViewById(R.id.infoBearing);
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.mapViewLayout);
	
		//Create map with tiles from OpenStreetMap and make overlays with tiles from OpenSeaMap
		mapView = new MyMapView(this, new MapnikTileDownloader());
		relative.addView(mapView);
		
		xmlParser = new MyXmlParser(this);

        configureMap();
        
        this.snapToLocationView = (ToggleButton) findViewById(R.id.snapToLocationView);
		this.snapToLocationView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isSnapToLocationEnabled()) {
					disableSnapToLocation();
				} else {
					enableSnapToLocation();
				}
			}
		});
		
 	}
		
	private void configureMap(){
		this.mapView.setClickable(true);
        
        //Displays ZoomControls on the map
        this.mapView.setBuiltInZoomControls(true);
        
        //Display scale on the map
        this.mapView.getMapScaleBar().setShowMapScaleBar(true);
		
        // get the map controller for this MapView
		this.mapController = this.mapView.getController();
		
		// get the pointers to different system services
		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		this.myLocationListener = new MyLocationListener(this);
		
		// set up the paint objects for the location overlay
		this.circleOverlayFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.circleOverlayFill.setStyle(Paint.Style.FILL);
		this.circleOverlayFill.setColor(Color.BLUE);
		this.circleOverlayFill.setAlpha(48);

		this.circleOverlayOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.circleOverlayOutline.setStyle(Paint.Style.STROKE);
		this.circleOverlayOutline.setColor(Color.BLUE);
		this.circleOverlayOutline.setAlpha(128);
		this.circleOverlayOutline.setStrokeWidth(2);
		
		// Get Orientation with magnetic field
		//sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		//this.mySensorListener = new MySensorListener(this,sensorManager);
		
		showMyLocation(true);
		showBalise();
		
		//enableSnapToLocation();
	}
	
	private void showBalise(){
		baliseOverlay = xmlParser.getBalises();
		if(baliseOverlay != null) this.mapView.getOverlays().add(baliseOverlay);
	}
	
	private void hiddenBalise(){
		this.mapView.getOverlays().remove(baliseOverlay);
	}
	
	private void showMyLocation(boolean centerAtFirstFix){
		//Get the best provider
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = this.locationManager.getBestProvider(criteria, true);

		if (bestProvider == null) {
			showDialog(DIALOG_LOCATION_PROVIDER_DISABLED);
			Toast.makeText(this, "Need GPS", Toast.LENGTH_LONG).show();
			return;
		}
		
		//Initialize overlay for MyPosition
		this.circleOverlay = new ArrayCircleOverlay(this.circleOverlayFill, this.circleOverlayOutline);
		this.overlayCircle = new OverlayCircle();
		this.circleOverlay.addCircle(this.overlayCircle);
		this.mapView.getOverlays().add(this.circleOverlay);

		this.itemizedOverlay = new ArrayItemizedOverlay(null);
		this.overlayItem = new OverlayItem();
		this.overlayItem.setMarker(ItemizedOverlay.boundCenter(getResources().getDrawable(R.drawable.bateau)));
		this.overlayItem.setTitle("My position");
		this.itemizedOverlay.addItem(this.overlayItem);
		this.mapView.getOverlays().add(this.itemizedOverlay);
		
		//Center the map to MyPosition
		this.myLocationListener.setCenterAtFirstFix(true);
		//Update MyPosition every second
		this.locationManager.requestLocationUpdates(bestProvider, 1000, 0, this.myLocationListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Returns the status of the "snap to location" mode.
	 * 
	 * @return true if the "snap to location" mode is enabled, false otherwise.
	 */
	boolean isSnapToLocationEnabled() {
		return this.snapToLocation;
	}
	
	/**
	 * Enables the "snap to location" mode.
	 */
	void enableSnapToLocation() {
		if (!this.snapToLocation) {
			this.snapToLocation = true;
			this.mapView.setClickable(false);
		}
	}
	
	/**
	 * Disables the "snap to location" mode.
	 */
	void disableSnapToLocation() {
		if (this.snapToLocation) {
			this.snapToLocation = false;
			this.mapView.setClickable(true);
		}
	}
	
}