package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.R;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DescriptionActivityText extends DescriptionActivity{
	
	private TextView descriptionTextView = null;
	private TextView contactTextView = null;
	private TextView capaciteTextView = null;
	private TextView serviceTextView = null;
	private ImageView mapImageView = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_text);
		
		descriptionTextView = (TextView) findViewById(R.id.Approche);
		descriptionTextView.setText("Approche du port : ...Approche du port : ...Approche du port : ...Approche du port : ...");
		
		contactTextView = (TextView) findViewById(R.id.Contact);
		contactTextView.setText("Contact du port : ...");
		
		mapImageView = (ImageView) findViewById(R.id.map);
		mapImageView.setImageDrawable(getResources().getDrawable(R.drawable.map_marina));
		
		capaciteTextView = (TextView) findViewById(R.id.Capacite);
		capaciteTextView.setText("Capacité du port : ...");
		
		serviceTextView = (TextView) findViewById(R.id.Service);
		serviceTextView.setText("Services du port : ...");
	}

}
