package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class DescriptionActivityText extends DescriptionActivity{
	
	private TextView mTextView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_text);
		
		mTextView = (TextView) findViewById(R.id.textView);
		mTextView.setText("Page détails");
	}

}
