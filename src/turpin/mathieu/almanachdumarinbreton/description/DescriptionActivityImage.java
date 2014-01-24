package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.R;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class DescriptionActivityImage extends DescriptionActivity{

	private ViewFlipper viewFlipper;
	private float lastX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_image);

		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
		ImageView iv = (ImageView) findViewById(R.id.imageView1);  
		iv.setOnTouchListener(new Touch()); 
		Toast.makeText(this, "Glisser pour changer d'horaire", Toast.LENGTH_LONG).show();
	}

	// Method to handle touch event like left to right swap and right to left swap
	public boolean onTouchEvent(MotionEvent touchevent)
	{
		switch (touchevent.getAction())
		{
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN:
		{
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP:
		{
			float currentX = touchevent.getX();
			int idChild = viewFlipper.getDisplayedChild();
			// if left to right swipe on screen
			if (lastX < currentX)
			{
				
				// If no more View/Child to flip
				if (idChild == 0)
					break;
				//Remove touch listener on currentView
				ImageView ivOld = (ImageView) ((LinearLayout) viewFlipper.getCurrentView()).getChildAt(1);
				ivOld.setOnTouchListener(null);

				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Left and current Screen will go OUT from Right
				viewFlipper.setInAnimation(this, R.anim.in_from_left);
				viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				
				//Add touch listener to previous View
				ImageView ivNew = (ImageView) ((LinearLayout) viewFlipper.getChildAt(idChild-1)).getChildAt(1);
				ivNew.setOnTouchListener(new Touch());
				
				// Show the previous Screen
				viewFlipper.showPrevious();
			}

			// if right to left swipe on screen
			if (lastX > currentX)
			{
				if (idChild == viewFlipper.getChildCount()-1)
					break;
				//Remove touch listener on currentView
				ImageView ivOld = (ImageView) ((LinearLayout) viewFlipper.getCurrentView()).getChildAt(1);
				ivOld.setOnTouchListener(null);
				
				// set the required Animation type to ViewFlipper
				// The Next screen will come in form Right and current Screen will go OUT from Left
				viewFlipper.setInAnimation(this, R.anim.in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				
				//Add touch listener to next View
				ImageView ivNew = (ImageView) ((LinearLayout) viewFlipper.getChildAt(idChild+1)).getChildAt(1);
				ivNew.setOnTouchListener(new Touch());
				
				// Show The next Screen
				viewFlipper.showNext();
			}
			break;
		}
		}
		return false;
	}

}
