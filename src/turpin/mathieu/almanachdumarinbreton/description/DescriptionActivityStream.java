package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.R.id;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;

public class DescriptionActivityStream extends DescriptionActivity{
	
	private ImageView streamPicture;
	private RadioButton streamButton0, 
						streamButton1, 
						streamButton2, 
						streamButton3, 
						streamButton4, 
						streamButton5, 
						streamButton6, 
						streamButton7, 
						streamButton8, 
						streamButton9, 
						streamButton10, 
						streamButton11, 
						streamButton12;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_stream);
		
		streamPicture = (ImageView) findViewById(R.id.streamView);  
		streamPicture.setOnTouchListener(new Touch()); 
		
		streamButton0 = (RadioButton) findViewById(id.streamButton0);
		streamButton1 = (RadioButton) findViewById(id.streamButton1);
		streamButton2 = (RadioButton) findViewById(id.streamButton2);
		streamButton3 = (RadioButton) findViewById(id.streamButton3);
		streamButton4 = (RadioButton) findViewById(id.streamButton4);
		streamButton5 = (RadioButton) findViewById(id.streamButton5);
		streamButton6 = (RadioButton) findViewById(id.streamButton6);
		streamButton7 = (RadioButton) findViewById(id.streamButton7);
		streamButton8 = (RadioButton) findViewById(id.streamButton8);
		streamButton9 = (RadioButton) findViewById(id.streamButton9);
		streamButton10 = (RadioButton) findViewById(id.streamButton10);
		streamButton11 = (RadioButton) findViewById(id.streamButton11);
		streamButton12 = (RadioButton) findViewById(id.streamButton12);
		
		streamButton0.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts0));
			}
		});
		
		streamButton1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts1));
			}
		});
		
		streamButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts2));
			}
		});
		
		streamButton3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts3));
			}
		});
		
		streamButton4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts4));
			}
		});
		
		streamButton5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts5));
			}
		});
		
		streamButton6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts6));
			}
		});
		
		streamButton7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts7));
			}
		});
		
		streamButton8.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts8));
			}
		});
		
		streamButton9.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts9));
			}
		});
		
		streamButton10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts10));
			}
		});
		
		streamButton11.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts11));
			}
		});
		
		streamButton12.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				streamPicture.setImageDrawable(getResources().getDrawable(R.drawable.ts12));
			}
		});
		
	}	
}
