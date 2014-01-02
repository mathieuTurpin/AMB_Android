package turpin.mathieu.almanachdumarinbreton;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MyMapView;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.MapnikTileDownloader;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
//import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	
	OverlayCircle overlayCircle;
	OverlayItem myPositionItem;
	private Paint circleOverlayFill;
	private Paint circleOverlayOutline;

	//Sensor for orientation
	//private SensorManager sensorManager;
	//MySensorListener mySensorListener;
	private boolean snapToLocation;

	TextView infoPosition;
	TextView infoSpeed;
	TextView infoBearing;
	
	private ToggleButton snapToLocationView;
	private Menu _menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoPosition = (TextView) findViewById(R.id.infoPosition);
		infoSpeed = (TextView) findViewById(R.id.infoSpeed);
		infoBearing = (TextView) findViewById(R.id.infoBearing);
		RelativeLayout relative = (RelativeLayout) findViewById(R.id.mapViewLayout);
	
		//Create map with tiles from OpenStreetMap and make overlays with tiles from OpenSeaMap
		//mapView = new MyMapView(this, new MapnikTileDownloader());
		
		//offline
		mapView = new MyMapView(this);
		String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		String cacheDirectoryPath = externalStorageDirectory + "/Android/data/org.mapsforge.android.maps/map/bretagne.map";
		mapView.setMapFile(new File(cacheDirectoryPath));

        configureMap();
		relative.addView(mapView);
        
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
		
        //Set zoom map to 14
        this.mapView.getController().setZoom(14);
        
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
	}
		
	/**
	 * Enables the "show my location" mode.
	 * 
	 * @param centerAtFirstFix
	 *            defines whether the map ({@link MyLocationListener}) should be centered to the first fix.
	 */
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
		this.overlayCircle = new OverlayCircle(this.circleOverlayFill, this.circleOverlayOutline);
		this.mapView.addCircle(this.overlayCircle);

		this.myPositionItem = new OverlayItem();
		this.myPositionItem.setMarker(ItemizedOverlay.boundCenter(getResources().getDrawable(R.drawable.bateau)));
		this.myPositionItem.setTitle("My position");
		this.myPositionItem.setSnippet("noTap");
		this.mapView.addItem(myPositionItem);
		
		//Center the map to MyPosition
		this.myLocationListener.setCenterAtFirstFix(true);
		//Update MyPosition every second
		this.locationManager.requestLocationUpdates(bestProvider, 1000, 0, this.myLocationListener);
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
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    _menu = menu;
	    
    	if(_menu.findItem(R.id.menu_OSM).isChecked()){
    		this.mapView.showBaliseOSM();
    	}
    	if(_menu.findItem(R.id.menu_service).isChecked()){
    		this.mapView.showService();
    	}
    	if(_menu.findItem(R.id.menu_ponton).isChecked()){
    		this.mapView.showText();
    	}
    	if(_menu.findItem(R.id.menu_sounding).isChecked()){
    		this.mapView.showSounding();
    	}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.menu_port:
                // Button behavior "Port"
                return true;
            case R.id.menu_marina:
                // Button behavior "Marina"
            	_menu.findItem(R.id.menu_port).setTitle(item.getTitle());
                this.mapView.getController().setZoom(16);
                this.mapView.setCenter(new GeoPoint(48.377972, -4.491666));
                return true;
            case R.id.menu_mode:
                // Button behavior "Mode"
            	Toast.makeText(this, "Mode", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_affichage:
                // Button behavior "Affichage"
                return true;
            case R.id.menu_OSM:
                // Button behavior "OSM"
            	if(item.isChecked()){
            		item.setChecked(false);
            		this.mapView.hiddenBalise();
            	}
            	else{
            		item.setChecked(true);
            		this.mapView.showBaliseOSM();
            	}
                return true;
            case R.id.menu_service:
                // Button behavior "Service"
            	if(item.isChecked()){
            		item.setChecked(false);
            		this.mapView.hiddenService();
            	}
            	else{
            		item.setChecked(true);
            		this.mapView.showService();
            	}
                return true;
            case R.id.menu_ponton:
                // Button behavior "Ponton"
            	if(item.isChecked()){
            		item.setChecked(false);
            		this.mapView.hiddenText();
            	}
            	else{
            		item.setChecked(true);
            		this.mapView.showText();
            	}
                return true;
            case R.id.menu_sounding:
                // Button behavior "Sounding"
            	if(item.isChecked()){
            		item.setChecked(false);
            		this.mapView.hiddenSounding();
            	}
            	else{
            		item.setChecked(true);
            		this.mapView.showSounding();
            	}
                return true;
            case R.id.menu_compte:
                // Button behavior "Compte"
    			Toast.makeText(this, "Compte", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
	}
	
}