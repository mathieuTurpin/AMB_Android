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

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import turpin.mathieu.almanachdumarinbreton.asynctask.AddPoiAsyncTask.AddPoiListener;
import turpin.mathieu.almanachdumarinbreton.description.DescriptionActivityWebLocal;
import turpin.mathieu.almanachdumarinbreton.forum.AccountActivity;
import turpin.mathieu.almanachdumarinbreton.forum.AccountManager;
import turpin.mathieu.almanachdumarinbreton.forum.ForumActivity;
import turpin.mathieu.almanachdumarinbreton.forum.LoginDialog;
import turpin.mathieu.almanachdumarinbreton.overlay.InfoOverlayItemDialog.InfoOverlayItemDialogListener;
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

public class MainActivity extends MapActivity implements LoginDialog.LoginDialogListener, InfoOverlayItemDialogListener, AddPoiListener{
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
		if (savedInstanceState != null) {
			this.mode = savedInstanceState.getInt(MyActivity.EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = savedInstanceState.getString(MyActivity.EXTRA_COURT_PORT);
			this.port = savedInstanceState.getString(MyActivity.EXTRA_PORT);
			initActivity();
			showMyLocation = false;
		}
		else{
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

	private void initIntentForActivity(Intent intent){
		if (intent != null) {
			//Get parameters
			this.mode = intent.getIntExtra(MyActivity.EXTRA_MODE_MAP,R.id.map_offline);
			this.courtNamePort = intent.getStringExtra(MyActivity.EXTRA_COURT_PORT);
			this.port = intent.getStringExtra(MyActivity.EXTRA_PORT);
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
		if(_menu.findItem(R.id.menu_comment).isChecked()){
			this.mapView.showPoi();
		}
		initMenu();

		return true;
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
			goToPort(R.id.menu_marina);
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
		initIntentForActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(_menu != null){
			initMenu();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(MyActivity.EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		savedInstanceState.putString(MyActivity.EXTRA_COURT_PORT, this.courtNamePort);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			savedInstanceState.putInt(MyActivity.EXTRA_MODE_MAP, R.id.map_online);
		}
	}

	private void initIntent(Intent intent){
		intent.putExtra(MyActivity.EXTRA_PORT, _menu.findItem(R.id.menu_port).getTitle().toString());
		intent.putExtra(MyActivity.EXTRA_COURT_PORT, this.courtNamePort);
		String mode_connexion = _menu.findItem(R.id.menu_connexion).getTitle().toString();
		if(mode_connexion.equals(getResources().getString(R.string.menu_online))){
			intent.putExtra(MyActivity.EXTRA_MODE_MAP, R.id.map_online);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case MyActivity.RESULT_IS_LOGIN:
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
	public void setIsLogin() {
		Toast.makeText(this, "Authenfication réussie", Toast.LENGTH_SHORT).show();
		_menu.findItem(R.id.menu_compte).setTitle(R.string.menu_compte);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_marina:
			goToPort(R.id.menu_marina);
			return true;
		case R.id.map_description:
			goToDescription();
			return true;
		case R.id.menu_forum:
			goToForum();
			return true;
		case R.id.map_offline:
			this.mode = R.id.map_offline;
			setConnectionMode(false,item.getTitle());
			return true;
		case R.id.map_online:
			this.mode = R.id.map_online;
			setConnectionMode(true,item.getTitle());
			return true;
		case R.id.menu_compte:
			if(accountManager.isLoggedIn()){
				goToAccount();
			}
			else{
				goToLogin();
			}
			return true;
		case R.id.menu_OSM:
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
			if(item.isChecked()){
				item.setChecked(false);
				this.mapView.hiddenSounding();
			}
			else{
				item.setChecked(true);
				this.mapView.showSounding();
			}
			return true;
		case R.id.menu_comment:
			if(item.isChecked()){
				item.setChecked(false);
				this.mapView.hiddenPoi();
			}
			else{
				item.setChecked(true);
				this.mapView.showPoi();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void goToPort(int idPort){
		String namePort = _menu.findItem(idPort).getTitle().toString();
		_menu.findItem(R.id.menu_port).setTitle(namePort);
		switch(idPort){
		case R.id.menu_marina:
			this.port = namePort;
			this.courtNamePort = getResources().getString(R.string.name_marina);
			this.mapView.getController().setZoom(16);
			this.mapView.setCenter(new GeoPoint(48.377972, -4.491666));
			break;
		}
	}

	private void goToDescription(){
		// if no port is selected
		String namePort = _menu.findItem(R.id.menu_port).getTitle().toString();
		if(namePort.equals(getResources().getString(R.string.menu_port))){
			Toast.makeText(this, R.string.error_missing_port, Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, DescriptionActivityWebLocal.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void goToForum(){
		Intent intent = new Intent(this, ForumActivity.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}

	private void setConnectionMode(boolean online,CharSequence title){
		_menu.findItem(R.id.map_online).setEnabled(!online);
		_menu.findItem(R.id.map_offline).setEnabled(online);
		_menu.findItem(R.id.menu_connexion).setTitle(title);
		if(online){
			this.mapView.setMapGenerator(new MapnikTileDownloader());
		}
		else{
			this.mapView.setMapGenerator(new DatabaseRenderer());
		}
	}

	private void goToAccount(){
		Intent intent = new Intent(this, AccountActivity.class);
		initIntent(intent);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent, MyActivity.RESULT_IS_LOGIN);
	}

	private void goToLogin(){
		// Create an instance of the dialog fragment and show it
		LoginDialog dialog = new LoginDialog();
		dialog.show(getFragmentManager(), "LoginDialog");
	}

	@Override
	public void commentByIdCentreInteret(int id) {
		Intent afficheListeCommentaires = new Intent(this, ForumActivity.class);
		afficheListeCommentaires.putExtra(ForumActivity.EXTRA_ID_CENTRE, id);
		startActivity(afficheListeCommentaires);

	}

	@Override
	public void addPoi(PoiDTO poi,String contenu) {
		OverlayItem item = new OverlayItem();

		double lat = Double.parseDouble(poi.getLatitude());
		double lon = Double.parseDouble(poi.getLongitude());
		item.setPoint(new GeoPoint(lat,lon));

		int idDrawable = R.drawable.bon_plan;
		String type = poi.getType();

		if(type.equals("peche")){
			idDrawable = R.drawable.poisson;
		}
		else if(type.equals("securite")){
			idDrawable = R.drawable.attention;
		}
		item.setMarker(ItemizedOverlay.boundCenter(getResources().getDrawable(idDrawable)));

		item.setSnippet(contenu);
		item.setTitle(type);
		this.mapView.addPoi(item);
	}
}