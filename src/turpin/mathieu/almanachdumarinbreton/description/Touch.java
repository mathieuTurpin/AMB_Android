package turpin.mathieu.almanachdumarinbreton.description;
import android.graphics.Matrix;  
import android.graphics.PointF;  
import android.view.MotionEvent;  
import android.view.View;  
import android.view.View.OnTouchListener;  
import android.widget.ImageView;  

public class Touch implements OnTouchListener {  

	// These matrices will be used to move and zoom image  
	Matrix matrix = new Matrix();  
	Matrix savedMatrix = new Matrix();  

	// We can be in one of these 3 states  
	private final int NONE = 0;  
	private final int DRAG = 1;  
	private final int ZOOM = 2;  

	private final float maxScale = 5f;
	private final float minScale = 1f;
	private int mode = NONE;  

	// Remember some things for zooming  
	PointF start = new PointF();  
	PointF mid = new PointF();  
	float oldDist = 0.7f;  

	
	private float dx; // postTranslate X distance
	private float dy; // postTranslate Y distance
	private float[] matrixValues = new float[9];
	private float matrixX = 0; // X coordinate of matrix inside the ImageView
	private float matrixY = 0; // Y coordinate of matrix inside the ImageView
	private float width = 0; // width of drawable
	private float height = 0; // height of drawable

	@Override  
	public boolean onTouch(View v, MotionEvent event) {  
		ImageView view = (ImageView) v;  
		// Dump touch event to log  

		// Handle touch events here...  
		switch (event.getAction() & MotionEvent.ACTION_MASK) {  
		case MotionEvent.ACTION_DOWN:  
			savedMatrix.set(matrix);  
			start.set(event.getX(), event.getY());  
			mode = DRAG;  
			break;  
		case MotionEvent.ACTION_POINTER_DOWN:  
			oldDist = spacing(event);  
			if (oldDist > 10f) {  
				savedMatrix.set(matrix);  
				midPoint(mid, event);  
				mode = ZOOM;  
			}  
			break;  
		case MotionEvent.ACTION_UP:  
		case MotionEvent.ACTION_POINTER_UP:  
			mode = NONE;  
			break;  
		case MotionEvent.ACTION_MOVE:  
			if (mode == DRAG) {  
				matrix.set(savedMatrix);  
				
		        matrix.getValues(matrixValues);
		        matrixX = matrixValues[2];
		        matrixY = matrixValues[5];
		        width = matrixValues[0] * (((ImageView) view).getDrawable()
		                                .getIntrinsicWidth());
		        height = matrixValues[4] * (((ImageView) view).getDrawable()
		                                .getIntrinsicHeight());

		        dx = event.getX() - start.x;
		        dy = event.getY() - start.y;

		        //if image will go outside left bound
		        if (matrixX + dx + width< view.getWidth()/2){
		        	dx = -matrixX - view.getWidth()/2;
		        }
		        //if image will go outside right bound
		        if(matrixX + dx > view.getWidth()/2){
		        	dx = view.getWidth()/2 - matrixX;
		        }
		        //if image will go oustside top bound
		        if (matrixY + dy + height < view.getHeight()/2){
		        	dy = -matrixY - view.getHeight()/2;
		        }
		        //if image will go outside bottom bound
		        if(matrixY + dy > view.getHeight()/2){
		        	dy = view.getHeight()/2 - matrixY;
		        }
		        matrix.postTranslate(dx, dy);   
			} else if (mode == ZOOM) {  
				float newDist = spacing(event);  
				if (newDist > 10f) {  
					matrix.set(savedMatrix);  
					float scale = newDist / oldDist;  
					matrix.postScale(scale, scale, mid.x, mid.y);  
				}
				float[] f = new float[9];
				matrix.getValues(f);
				float scaleX = f[Matrix.MSCALE_X];
				float scaleY = f[Matrix.MSCALE_Y];

				if(scaleX <= minScale)
					matrix.postScale((minScale)/scaleX, (minScale)/scaleY, mid.x, mid.y);
				else if(scaleX >= maxScale) 
					matrix.postScale((maxScale)/scaleX, (maxScale)/scaleY, mid.x, mid.y);
			}  
			break;  
		}  

		view.setImageMatrix(matrix);  
		return true; // indicate event was handled  
	}

	/** Determine the space between the first two fingers */  
	private float spacing(MotionEvent event) {  
		float x = event.getX(0) - event.getX(1);  
		float y = event.getY(0) - event.getY(1);  
		return (float) Math.sqrt(x * x + y * y);  
	}  

	/** Calculate the mid point of the first two fingers */  
	private void midPoint(PointF point, MotionEvent event) {  
		float x = event.getX(0) + event.getX(1);  
		float y = event.getY(0) + event.getY(1);  
		point.set(x / 2, y / 2);  
	}  
} 