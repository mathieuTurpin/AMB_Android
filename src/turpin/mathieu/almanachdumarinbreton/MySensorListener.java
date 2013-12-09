package turpin.mathieu.almanachdumarinbreton;

import org.mapsforge.android.maps.overlay.ItemizedOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class MySensorListener implements SensorEventListener {

	// Current value
	float x;
	float currentX = 0;
	//float y, z;
	float[] acceleromterVector=new float[3];
	float[] magneticVector=new float[3];
	float[] resultMatrix=new float[9];
	float[] values=new float[3];
	
	//The magnetic field
	Sensor magnetic;
		
	// The accelerometer
	Sensor accelerometer;
	
	private final MainActivity myMapViewer;
	private SensorManager mySensorManager;
	private boolean test;
	private Handler handler;
	private Display display;
	private float currentRunX = 0;
	
	/**
	 * Sera utilisé plus tard dans le projet
	 * @param myMapViewer
	 * @param sensorManager
	 */
	public MySensorListener(MainActivity myMapViewer,SensorManager sensorManager){
		this.myMapViewer = myMapViewer;
		// Instantiate the SensorManager
		this.mySensorManager = sensorManager;
		// Instantiate the magnetic sensor and its max range
		magnetic = this.mySensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		// Instantiate the accelerometer
		accelerometer = this.mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		display = ((WindowManager)this.myMapViewer.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();		
		handler = new Handler();	     
	}
	
	void sendRequestUpdates() {
		handler.postDelayed(requestUpdater, 1000);
	}
		
	private Runnable requestUpdater = new Runnable(){
        @Override
        public void run(){
        	if(currentX!=currentRunX){   
        		currentRunX = currentX;
        		Drawable c = rotateDrawable(currentX);
                myMapViewer.overlayItem.setMarker(ItemizedOverlay.boundCenter(c));
                myMapViewer.mapView.updatePosition();
        	}
			sendRequestUpdates();
        }
	};

	public void registerSensor(){
		this.mySensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_UI);
		this.mySensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		sendRequestUpdates();
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
    	float tmp = event.values[0];

		switch (display.getRotation()) {
        case Surface.ROTATION_0:
            break;
        case Surface.ROTATION_90:
        	event.values[0] = -event.values[1];
        	event.values[1] = tmp;
            break;
        case Surface.ROTATION_180:
        	event.values[0] = -event.values[0];
        	event.values[1] = -event.values[1];
            break;
        case Surface.ROTATION_270:
        	event.values[0] = event.values[1];
        	event.values[1] = -tmp;
            break;
        }
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			acceleromterVector=event.values;
			test = false;
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magneticVector=event.values;
			test = true;
		}
		
		SensorManager.getRotationMatrix(resultMatrix, null, acceleromterVector, magneticVector);
		
		SensorManager.getOrientation(resultMatrix, values);
		x = (float) Math.toDegrees(values[0]);
	
		if(test && ((x-currentX) > 10 || (x-currentX) < -10)){
			currentX = x;
		}
	}
	
	public BitmapDrawable rotateDrawable(float angle)
	{
	  Bitmap arrowBitmap = BitmapFactory.decodeResource(this.myMapViewer.getResources(), R.drawable.bateau);
	 
	  // Create blank bitmap of equal size
	  Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
	  canvasBitmap.eraseColor(0x00000000);

	  // Create canvas
	  Canvas canvas = new Canvas(canvasBitmap);

	  // Create rotation matrix
	  Matrix rotateMatrix = new Matrix();
	  rotateMatrix.setRotate(angle, canvas.getWidth()/2, canvas.getHeight()/2);

	  // Draw bitmap onto canvas using matrix
	  canvas.drawBitmap(arrowBitmap, rotateMatrix, null);

	  return new BitmapDrawable(canvasBitmap); 
	}

}
