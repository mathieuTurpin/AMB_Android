package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class DescriptionActivityWeb extends DescriptionActivity{

	private WebView mWebView = null;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Adds Progrss bar Support
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_description_web);

		// Makes Progress bar Visible
		getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

		mWebView = (WebView) findViewById(R.id.webView);
		final DescriptionActivityWeb MyActivity = this;
		mWebView.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress)  
			{
				//Make the bar disappear after URL is loaded, and changes string to Loading...
				MyActivity.setTitle("Loading...");
				MyActivity.setProgress(progress * 100); //Make the bar disappear after URL is loaded

				// Return the app name after finish loading
				if(progress == 100)
					MyActivity.setTitle("");
			}
		});
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
				
		Intent intent = getIntent();
		//Orientation change
		if (savedInstanceState != null) {
			url = savedInstanceState.getString(EXTRA_URL);
		}
		else{
			url = intent.getStringExtra(EXTRA_URL);
		}
		
		if(url != null){
			mWebView.loadUrl(url);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString(EXTRA_URL,url);
	}

}
