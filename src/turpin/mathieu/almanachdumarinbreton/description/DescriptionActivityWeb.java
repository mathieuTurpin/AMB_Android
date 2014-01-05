package turpin.mathieu.almanachdumarinbreton.description;

import turpin.mathieu.almanachdumarinbreton.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class DescriptionActivityWeb extends DescriptionActivity{
	
	private WebView mWebView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_web);
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setWebViewClient(new WebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		
		Intent intent = getIntent();
		if (intent != null) {
			String url = intent.getStringExtra(EXTRA_URL);
			if(url != null){
				mWebView.loadUrl(url);
			}
		}
	}

}
