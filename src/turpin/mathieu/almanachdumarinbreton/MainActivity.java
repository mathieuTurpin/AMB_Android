package turpin.mathieu.almanachdumarinbreton;

import java.io.File;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MyMapView;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.android.maps.mapgenerator.tiledownloader.MapnikTileDownloader;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayCircle;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;

import turpin.mathieu.almanachdumarinbreton.description.DescriptionActivityWebLocal;
import turpin.mathieu.almanachdumarinbreton.forum.AccountActivity;
import turpin.mathieu.almanachdumarinbreton.forum.AccountManager;
import turpin.mathieu.almanachdumarinbreton.forum.ForumActivity;
import turpin.mathieu.almanachdumarinbreton.forum.LoginDialog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends MapActivity implements LoginDialog.LoginDialogListener{
	private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;

	public MyMapView mapView;
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;

	private String cacheDirectoryPath;

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

	//Extra
	final String EXTRA_PORT = "port_name";
	final int RESULT_IS_LOGIN = 0;
	final String EXTRA_COURT_PORT = "port_court_name";
	final String EXTRA_MODE_MAP = "mode_map";

	private int mode;
	private String courtNamePort ="";
	private String port ="";

	private AccountManager accountManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		infoPosition = (TextView) findViewById(R.id.infoPosition);
		infoSpeed = (TextView) findViewById(R.id.infoSpeed);
		infoBearing = (TextView) findViewById(R.id.infoBearing);

		RelativeLayout relative = (RelativeLayout) findViewById(R.id.mapViewLayout);

		this.mapView = new MyMapView(this);
		String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
		cacheDirectoryPath = externalStorageDirectory + "/Android/data/org.mapsforge.android.maps/map/bretagne.map";
		this.mapView.setMapFile(new File(cacheDirectoryPath));		
		this.mapView.getFileSystemTileCache().setPersistent(false);

		boolean showMyLocation = true;
		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null && intent.getExtras() == null) {
			this.mode = savedInstanceState.getInt(EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = savedInstanceState.getString(EXTRA_COURT_PORT);
			this.port = savedInstanceState.getString(EXTRA_PORT);
			initActivity();
			showMyLocation = false;
		}
		else{
			if(intent.getExtras() != null){
				showMyLocation = false;
			}
			initIntentForActivity(intent);
		}

		configureMap();

		relative.addView(this.mapView);

		initLocation(showMyLocation);

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
	}

	private void initLocation(boolean showMyLocation){
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

		showMyLocation(showMyLocation);
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
		this.myLocationListener.setCenterAtFirstFix(centerAtFirstFix);
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
		initMenu();

		return true;
	}
	
	private void initIntentForActivity(Intent intent){
		if (intent != null) {
			//Get parameters
			this.mode = intent.getIntExtra(EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = intent.getStringExtra(EXTRA_COURT_PORT);
			this.port = intent.getStringExtra(EXTRA_PORT);
			initActivity();
		}
		else{
			this.mapView.setMapGenerator(new DatabaseRenderer());
		}
	}
	
	private void initActivity(){
		if(this.mode == R.id.map_online){
			this.mapView.setMapGenerator(new MapnikTileDownloader());
		}
		else{
			this.mapView.setMapGenerator(new DatabaseRenderer());
		}
	}
	
	private void initMenu(){
		if(this.mode == R.id.map_online){
			_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_online);
			_menu.findItem(R.id.map_online).setEnabled(false);
			_menu.findItem(R.id.map_offline).setEnabled(true);
		}
		else{
			_menu.findItem(R.id.menu_connexion).setTitle(R.string.menu_offline);
			_menu.findItem(R.id.map_online).setEnabled(true);
			_menu.findItem(R.id.map_offline).setEnabled(false);
		}
		
		if(this.port != null && port.equals(getResources().getString(R.string.menu_marina))){
			goToMarina(this.port);
		}
		else{
			_menu.findItem(R.id.menu_port).setTitle(R.string.menu_port);
		}
		
		accountManager = new AccountManager(getApplicationContext());

		if(accountManager.isLoggedIn()){
			_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_compte);
		}
		else{
			_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_login);
		}
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		//To check if is not orientation change
		if(intent.getExtras() != null){
			initIntentForActivity(intent);
			if(_menu != null){
				initMenu();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		savedInstanceState.putString(EXTRA_COURT_PORT, this.courtNamePort);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			savedInstanceState.putInt(EXTRA_MODE_MAP, R.id.map_online);
		}
	}

	private void goToMarina(String name){
		_menu.findItem(R.id.menu_port).setTitle(name);
		this.courtNamePort = getResources().getString(R.string.name_marina);
		this.mapView.getController().setZoom(16);
		this.mapView.setCenter(new GeoPoint(48.377972, -4.491666));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case R.id.menu_marina:
			// Button behavior "Marina"
			goToMarina(item.getTitle().toString());
			return true;
		case R.id.map_offline:
			// Button behavior "Map offline"
			_menu.findItem(R.id.map_online).setEnabled(true);
			_menu.findItem(R.id.map_offline).setEnabled(false);
			_menu.findItem(R.id.menu_connexion).setTitle(item.getTitle());
			this.mapView.setMapGenerator(new DatabaseRenderer());
			return true;
		case R.id.map_online:
			// Button behavior "Map Online"
			_menu.findItem(R.id.map_online).setEnabled(false);
			_menu.findItem(R.id.map_offline).setEnabled(true);
			_menu.findItem(R.id.menu_connexion).setTitle(item.getTitle());
			this.mapView.setMapGenerator(new MapnikTileDownloader());
			return true;
		case R.id.map_description:
			// Button behavior "Map Decription"
			// if no port is selected
			String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
			if(namePort.equals(getResources().getString(R.string.menu_port))){
				Toast.makeText(MainActivity.this, R.string.error_missing_port, Toast.LENGTH_SHORT).show();
				return true;
			}

			intent = new Intent(MainActivity.this, DescriptionActivityWebLocal.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			initIntent(intent);

			startActivity(intent);
			return true;
		case R.id.menu_forum:
			intent = new Intent(MainActivity.this, ForumActivity.class);
			initIntent(intent);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
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
			if(accountManager.isLoggedIn()){
				intent = new Intent(MainActivity.this, AccountActivity.class);
				initIntent(intent);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivityForResult(intent, RESULT_IS_LOGIN);
			}
			else{
				// Create an instance of the dialog fragment and show it
				LoginDialog dialog = new LoginDialog();
				dialog.show(getFragmentManager(), "LoginDialog");
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initIntent(Intent intent){
		intent.putExtra(EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		intent.putExtra(EXTRA_COURT_PORT, this.courtNamePort);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			intent.putExtra(EXTRA_MODE_MAP, R.id.map_online);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case RESULT_IS_LOGIN:
			switch(resultCode){
			//Logout
			case Activity.RESULT_CANCELED:
				_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_login);
				break;
				//Always login
			case Activity.RESULT_OK:
				//Nothing to do
				break;
			}
			break;
		default:
			return;
		}
	}

	@Override
	public void setIsLogin() {
		_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_compte);
	}

}